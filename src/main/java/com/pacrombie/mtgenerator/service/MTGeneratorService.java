package com.pacrombie.mtgenerator.service;

import com.pacrombie.mtgenerator.downstream.scryfall.ScryfallRestClient;
import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.ScryfallCardData;
import com.pacrombie.mtgenerator.repository.CardRepository;
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
    private final ScryfallRestClient scryfallRestClient;

    public String generateDeck() {

        List<String> deckList = new ArrayList<>();

        CardEntity commander = getRandomCommander();

        deckList.add(commander.getName());
        deckList.addAll(generateManaBase(36, commander.getColorIdentity()));
        deckList.addAll(generateNonLands(63, commander));

        return formatDeckList(deckList);
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

        // Planeswalkers
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

    private List<String> generateManaBase(int landCount, Set<String> colorIdentity) {

        if (colorIdentity.isEmpty()) {
            return List.of(landCount + " Wastes");
        }

        Map<String, String> basicLandByColor = Map.of(
                "W", "Plains",
                "U", "Island",
                "B", "Swamp",
                "R", "Mountain",
                "G", "Forest"
        );

        List<String> lands = new ArrayList<>();

        int landsPerColor = landCount / colorIdentity.size();
        int remainder = landCount % colorIdentity.size();

        for (String color : colorIdentity) {
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

        List<CardEntity> candidates = cardRepository.findByCommanderLegalTrue()
                .stream()
                .filter(card -> !card.getId().equals(commander.getId()))
                .filter(card -> card.getTypeLine() != null)
                .filter(card -> !card.getTypeLine().contains("Land"))
                .filter(card -> commander.getColorIdentity().containsAll(card.getColorIdentity()))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(candidates);

        return candidates.stream()
                .limit(63)
                .map(CardEntity::getName)
                .toList();
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
