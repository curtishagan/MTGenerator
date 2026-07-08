package com.pacrombie.mtgenerator.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeckContext {

    private CardEntity commander;

    private List<CardEntity> cards = new ArrayList<>();

    public int countCardsWithTagContaining(String tagPart) {
        if (cards == null) {
            return 0;
        }

        return (int) cards.stream()
                .filter(card -> card.getOracleTags() != null)
                .filter(card -> card.getOracleTags().stream()
                        .anyMatch(tag -> tag.toLowerCase().contains(tagPart.toLowerCase())))
                .count();
    }
}
