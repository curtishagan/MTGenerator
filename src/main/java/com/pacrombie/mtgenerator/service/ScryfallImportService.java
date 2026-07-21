package com.pacrombie.mtgenerator.service;

import com.pacrombie.mtgenerator.downstream.scryfall.ScryfallRestClient;
import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.ScryfallCardData;
import com.pacrombie.mtgenerator.model.ScryfallOracleTagData;
import com.pacrombie.mtgenerator.model.ScryfallTaggingData;
import com.pacrombie.mtgenerator.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ScryfallImportService {

    private final CardRepository cardRepository;
    private final ScryfallRestClient scryfallRestClient;

    public void importScryfallCards() {

        cardRepository.deleteAll();

        if (cardRepository.count() > 0) {
            throw new IllegalStateException("Repository is not empty");
        }

        log.info("Downloading cards from Scryfall...");

        ScryfallCardData[] cards = scryfallRestClient.getCardData();

        if (cards == null) {
            throw new IllegalStateException("No cards downloaded from Scryfall");
        }

        List<CardEntity> entities = Arrays.stream(cards)
                .filter(card -> card.getLegalities() != null)
                .filter(card -> "legal".equals(card.getLegalities().get("commander")))
                .map(this::toEntity)
                .toList();

        log.info("Saving cards...");
        cardRepository.saveAll(entities);
        log.info("All {} cards saved!", entities.size());

        importOracleTags();
    }

    private void importOracleTags() {
        log.info("Downloading oracle tags from Scryfall...");

        ScryfallOracleTagData[] tags = scryfallRestClient.getOracleTagData();

        if (tags == null) {
            throw new IllegalStateException("No oracle tags downloaded from Scryfall");
        }

        log.info("Loading saved cards for tag mapping...");

        Map<String, List<CardEntity>> cardsByOracleId = cardRepository.findAll()
                .stream()
                .filter(card -> card.getOracleId() != null)
                .collect(Collectors.groupingBy(CardEntity::getOracleId));

        for (ScryfallOracleTagData tag : tags) {
            if (tag.getSlug() == null || tag.getTaggings() == null) {
                continue;
            }

            for (ScryfallTaggingData tagging : tag.getTaggings()) {
                if (tagging.getOracleId() == null) {
                    continue;
                }

                List<CardEntity> matchingCards = cardsByOracleId.get(tagging.getOracleId());

                if (matchingCards == null) {
                    continue;
                }

                for (CardEntity card : matchingCards) {
                    card.getOracleTags().add(tag.getSlug());
                }
            }
        }

        List<CardEntity> updatedCards = cardsByOracleId.values()
                .stream()
                .flatMap(List::stream)
                .toList();

        log.info("Saving oracle tags onto cards...");
        cardRepository.saveAll(updatedCards);
        log.info("Oracle tags imported.");
    }

    private CardEntity toEntity(ScryfallCardData card) {
        CardEntity entity = new CardEntity();

        entity.setId(card.getId());
        entity.setOracleId(card.getOracleId());
        entity.setName(card.getName());
        entity.setManaCost(card.getManaCost());
        entity.setManaValue(card.getCmc());
        entity.setTypeLine(card.getTypeLine());
        entity.setOracleText(card.getOracleText());
        entity.setColorIdentity(card.getColorIdentity());
        entity.setProducedMana(card.getProducedMana());
        entity.setLayout(card.getLayout());
        entity.setOracleTags(new HashSet<>());
        entity.setEdhrecRank(card.getEdhrecRank());

        entity.setCommanderLegal(
                card.getLegalities() != null
                        && "legal".equals(card.getLegalities().get("commander"))
        );

        return entity;
    }
}