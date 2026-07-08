package com.pacrombie.mtgenerator.service.scoring.rule;

import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;
import org.springframework.stereotype.Component;

@Component
public class RampScoringRule implements CardScoringRule {

    private static final int TARGET_RAMP_COUNT = 10;

    @Override
    public int score(CardEntity card, DeckContext context) {
        if (!hasTagContaining(card, "ramp")) {
            return 0;
        }

        int currentRampCount = context.countCardsWithTagContaining("ramp");

        if (currentRampCount >= TARGET_RAMP_COUNT) {
            return 0;
        }

        int rampNeeded = TARGET_RAMP_COUNT - currentRampCount;

        return rampNeeded * 10;
    }

    private boolean hasTagContaining(CardEntity card, String tagPart) {
        if (card.getOracleTags() == null) {
            return false;
        }

        return card.getOracleTags().stream()
                .anyMatch(tag -> tag.toLowerCase().contains(tagPart.toLowerCase()));
    }
}
