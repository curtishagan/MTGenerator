package com.pacrombie.mtgenerator.service;

import com.pacrombie.mtgenerator.downstream.scryfall.ScryfallRestClient;
import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.ScryfallCardData;
import com.pacrombie.mtgenerator.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ScryfallImportService {

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

    private CardEntity toEntity(ScryfallCardData card) {
        CardEntity entity = new CardEntity();

        entity.setId(card.getId());
        entity.setName(card.getName());
        entity.setManaCost(card.getManaCost());
        entity.setManaValue(card.getCmc());
        entity.setTypeLine(card.getTypeLine());
        entity.setOracleText(card.getOracleText());
        entity.setColorIdentity(card.getColorIdentity());
        entity.setProducedMana(card.getProducedMana());

        entity.setCommanderLegal(
                card.getLegalities() != null
                        && "legal".equals(card.getLegalities().get("commander"))
        );

        return entity;
    }
}
