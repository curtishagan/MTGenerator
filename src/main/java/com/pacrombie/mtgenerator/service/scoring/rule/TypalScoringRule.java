package com.pacrombie.mtgenerator.service.scoring.rule;

import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TypalScoringRule implements CardScoringRule {

    private static final int TYPAL_TAG_MATCH_SCORE = 60;
    private static final int TYPE_LINE_MATCH_SCORE = 35;

    @Override
    public int score(CardEntity card, DeckContext context) {
        if (card.getOracleTags() == null
                || context.getCommander() == null
                || context.getCommander().getOracleTags() == null) {
            return 0;
        }

        Set<String> commanderTypalTags = context.getCommander().getOracleTags()
                .stream()
                .filter(tag -> tag.toLowerCase().startsWith("typal-"))
                .collect(Collectors.toSet());

        if (commanderTypalTags.isEmpty()) {
            return 0;
        }

        int score = 0;

        for (String typalTag : commanderTypalTags) {
            String creatureType = typalTag
                    .replace("typal-", "")
                    .replace("-", " ");

            if (card.getOracleTags().contains(typalTag)) {
                score += TYPAL_TAG_MATCH_SCORE;
            }

            if (card.getTypeLine() != null
                    && card.getTypeLine().toLowerCase().contains(creatureType.toLowerCase())) {
                score += TYPE_LINE_MATCH_SCORE;
            }
        }

        return score;
    }
}