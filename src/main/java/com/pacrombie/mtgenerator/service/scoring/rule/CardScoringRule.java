package com.pacrombie.mtgenerator.service.scoring.rule;

import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;

public interface CardScoringRule {
    int score(CardEntity card, DeckContext context);
}
