package com.pacrombie.mtgenerator.service.scoring.rule;

import com.pacrombie.mtgenerator.config.TagCardScoreFileLoader;
import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TagCardScoreRule implements CardScoringRule {

    private final Map<String, Integer> tagScores;

    public TagCardScoreRule(TagCardScoreFileLoader loader) {
        this.tagScores = loader.loadTagScores();
    }

    @Override
    public int score(CardEntity card, DeckContext currentDeck) {
        if (card.getOracleTags() == null || card.getOracleTags().isEmpty()) {
            return 0;
        }

        int score = 0;

        for (String tag : card.getOracleTags()) {
            score += tagScores.getOrDefault(normalize(tag), 0);
        }

        return score;
    }

    private String normalize(String tag) {
        return tag
                .replace("\"", "")
                .trim()
                .toLowerCase();
    }
}