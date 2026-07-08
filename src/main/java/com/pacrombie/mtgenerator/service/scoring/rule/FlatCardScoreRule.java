package com.pacrombie.mtgenerator.service.scoring.rule;

import com.pacrombie.mtgenerator.config.FlatCardScoreFileLoader;
import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FlatCardScoreRule implements CardScoringRule {

    private final Map<String, Integer> cardScores;

    public FlatCardScoreRule(FlatCardScoreFileLoader loader) {
        this.cardScores = loader.loadCardScores();
    }

    @Override
    public int score(CardEntity card, DeckContext currentDeck) {
        String cardName = normalize(card.getName());
        return cardScores.getOrDefault(cardName, 0);
    }

    private String normalize(String name) {
        return name.trim().toLowerCase();
    }
}
