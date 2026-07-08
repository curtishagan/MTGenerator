package com.pacrombie.mtgenerator.service;

import com.pacrombie.mtgenerator.downstream.scryfall.ScryfallRestClient;
import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;
import com.pacrombie.mtgenerator.model.DeckRequest;
import com.pacrombie.mtgenerator.model.ScryfallCardData;
import com.pacrombie.mtgenerator.repository.CardRepository;
import com.pacrombie.mtgenerator.service.scoring.CardScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class MTGeneratorService {

    private final CardRepository cardRepository;
    private final CardScoringService cardScoringService;

    public String generateDeck(DeckRequest deckRequest) {

        List<String> pickedCards = new ArrayList<>();

        CardEntity commander;

        if (deckRequest.getCommander() != null) {
            commander = cardRepository.findByNameIgnoreCase(deckRequest.getCommander())
                    .orElseThrow(() -> new IllegalArgumentException("Provided commander could not be found."));
        } else {
            commander = getRandomCommander();
        }

        pickedCards.add(commander.getName());
        pickedCards.addAll(generateManaBase(36, commander));
        pickedCards.addAll(generateNonLands(63, commander));

        return formatDeckList(pickedCards);
    }

    private CardEntity getRandomCommander() {

        List<CardEntity> candidates = new ArrayList<>();

        // All Legendary Creatures
        candidates.addAll(cardRepository.findByCommanderLegalTrue()
                .stream()
                .filter(card -> card.getTypeLine().contains("Legendary"))
                .filter(card -> card.getTypeLine().contains("Creature"))
                .toList()
        );

        // Planeswalker Commanders
        candidates.addAll(cardRepository.findByCommanderLegalTrue()
                .stream()
                .filter(card -> card.getOracleText() != null)
                .filter(card -> card.getOracleText()
                        .toLowerCase()
                        .contains("can be your commander"))
                .toList()
        );

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No commanders found.");
        }

        return candidates.get(
                ThreadLocalRandom.current().nextInt(candidates.size())
        );
    }

    private List<String> generateManaBase(int landCount, CardEntity commander) {
        if (commander.getColorIdentity().isEmpty()) {
            return List.of(landCount + " Wastes");
        }

        List<String> lands = new ArrayList<>();

        int nonBasicCount = Math.min(landCount / 3, 12);

        List<CardEntity> nonBasicCandidates = cardRepository.findByCommanderLegalTrue()
                .stream()
                .filter(card -> card.getTypeLine() != null)
                .filter(card -> card.getTypeLine().contains("Land"))
                .filter(card -> !card.getTypeLine().contains("Basic"))
                .filter(card -> card.getColorIdentity() != null)
                .filter(card -> commander.getColorIdentity().containsAll(card.getColorIdentity()))
                .filter(card -> card.getProducedMana() != null)
                .filter(card -> !card.getProducedMana().isEmpty())
                .filter(card -> card.getProducedMana().stream().anyMatch(commander.getColorIdentity()::contains))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(nonBasicCandidates);

        List<String> nonBasics = nonBasicCandidates.stream()
                .limit(nonBasicCount)
                .map(card -> "1 " + card.getName())
                .toList();

        lands.addAll(nonBasics);

        int remainingBasics = landCount - nonBasics.size();

        lands.addAll(generateBasics(remainingBasics, commander));

        return lands;
    }

    private List<String> generateBasics(int landCount, CardEntity commander) {
        Map<String, String> basicLandByColor = Map.of(
                "W", "Plains",
                "U", "Island",
                "B", "Swamp",
                "R", "Mountain",
                "G", "Forest"
        );

        List<String> lands = new ArrayList<>();

        int landsPerColor = landCount / commander.getColorIdentity().size();
        int remainder = landCount % commander.getColorIdentity().size();

        for (String color : commander.getColorIdentity()) {
            String landName = basicLandByColor.get(color);

            int count = landsPerColor;

            if (remainder > 0) {
                count++;
                remainder--;
            }

            lands.add(count + " " + landName);
        }

        return lands;
    }

    private List<String> generateNonLands(int cardCount, CardEntity commander) {

        List<CardEntity> candidates = getLegalNonLandCards(commander);

        List<CardEntity> pickedCards = new ArrayList<>();

        while (pickedCards.size() < cardCount && !candidates.isEmpty()) {
            DeckContext context = new DeckContext();
            context.setCommander(commander);
            context.setCards(pickedCards);

            CardEntity picked = pickScoredCard(candidates, context);

            pickedCards.add(picked);
            candidates.remove(picked);
        }

        return pickedCards.stream()
                .map(CardEntity::getName)
                .toList();
    }

    private CardEntity pickScoredCard(List<CardEntity> candidates, DeckContext context) {
        List<CardEntity> topChoices = candidates.stream()
                .sorted(Comparator
                        .comparingInt((CardEntity card) -> cardScoringService.score(card, context))
                        .reversed())
                .limit(20)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(topChoices);

        return topChoices.getFirst();
    }

    private List<CardEntity> getLegalNonLandCards(CardEntity commander) {
        return cardRepository.findByCommanderLegalTrue()
                .stream()
                .filter(this::isNormalPlayableCard)
                .filter(card -> !card.getId().equals(commander.getId()))
                .filter(card -> card.getTypeLine() != null)
                .filter(card -> !card.getTypeLine().contains("Land"))
                .filter(card -> commander.getColorIdentity().containsAll(card.getColorIdentity()))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isNormalPlayableCard(CardEntity card) {
        if (card.getLayout() == null) {
            return false;
        }

        boolean playableLayout = switch (card.getLayout()) {
            case "normal",
                 "split",
                 "transform",
                 "modal_dfc",
                 "adventure",
                 "meld",
                 "flip",
                 "leveler",
                 "class",
                 "saga",
                 "battle" -> true;
            default -> false;
        };

        if (!playableLayout) {
            return false;
        }

        String typeLine = card.getTypeLine() == null ? "" : card.getTypeLine().toLowerCase();
        String oracleText = card.getOracleText() == null ? "" : card.getOracleText().toLowerCase();

        return !typeLine.contains("sticker")
                && !typeLine.contains("attraction")
                && !oracleText.contains("sticker")
                && !oracleText.contains("attraction");
    }

    private String formatDeckList(List<String> deckList) {
        String deckListString = "";

        for (String card : deckList) {
            if (!card.matches("^(100|[1-9][0-9]?)\\b.*"))
                deckListString = deckListString.concat("1 ");

            deckListString = deckListString.concat(card);
            deckListString = deckListString.concat("\n");
        }

        return deckListString;
    }
}
