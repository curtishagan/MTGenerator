package com.pacrombie.mtgenerator.service.scoring.rule;

import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SharedCommanderTagScoringRule implements CardScoringRule {

    @Override
    public int score(CardEntity card, DeckContext context) {
        if (card.getOracleTags() == null
                || context.getCommander() == null
                || context.getCommander().getOracleTags() == null) {
            return 0;
        }

        Set<String> sharedTags = new HashSet<>(card.getOracleTags());
        sharedTags.retainAll(context.getCommander().getOracleTags());

        return sharedTags.size();
    }
}
