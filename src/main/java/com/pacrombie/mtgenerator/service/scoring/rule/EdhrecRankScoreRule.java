package com.pacrombie.mtgenerator.service.scoring.rule;

import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;
import org.springframework.stereotype.Component;

@Component
public class EdhrecRankScoreRule implements CardScoringRule {

    @Override
    public int score(CardEntity card, DeckContext context) {
        Integer rank = card.getEdhrecRank();

        if (rank == null) {
            return 0;
        }

        if (rank <= 100) {
            return 5;
        }

        if (rank <= 500) {
            return 4;
        }

        if (rank <= 2_000) {
            return 3;
        }

        if (rank <= 5_000) {
            return 2;
        }

        if (rank <= 10_000) {
            return 1;
        }

        return 0;
    }
}