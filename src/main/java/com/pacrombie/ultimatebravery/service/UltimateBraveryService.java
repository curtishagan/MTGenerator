package com.pacrombie.ultimatebravery.service;

import com.pacrombie.ultimatebravery.downstream.scryfall.ScryfallRestClient;
import com.pacrombie.ultimatebravery.model.CardEntity;
import com.pacrombie.ultimatebravery.model.DeckRequest;
import com.pacrombie.ultimatebravery.model.ScryfallCardData;
import com.pacrombie.ultimatebravery.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
@Slf4j
public class UltimateBraveryService {

    private final CardRepository cardRepository;
    private final ScryfallRestClient scryfallRestClient;

    public void importScryfallCards() {

        log.info("Downloading from Scryfall...");

        ScryfallCardData[] cards = scryfallRestClient.getCardData();

        if (cards == null) {
            throw new IllegalStateException("No cards downloaded from Scryfall");
        }

        List<CardEntity> entities = Arrays.stream(cards)
                .filter(card -> card.getLegalities() != null)
                .filter(card -> "legal".equals(card.getLegalities().get("commander")))
                .map(this::toEntity)
                .toList();

        log.info("Starting save...");
        cardRepository.saveAll(entities);
        log.info("All " + entities.size() + " cards saved!");

    }

    public String generateDeck() {

        List<CardEntity> cards = cardRepository.findByCommanderLegalTrue();

        String deckList = cards.get(ThreadLocalRandom.current().nextInt(cards.size())).getName();

        return deckList;
    }

    private CardEntity toEntity(ScryfallCardData card) {
        CardEntity entity = new CardEntity();

        entity.setId(card.getId());
        entity.setName(card.getName());

        entity.setCommanderLegal(
                card.getLegalities() != null
                        && "legal".equals(card.getLegalities().get("commander"))
        );

        return entity;
    }
}
