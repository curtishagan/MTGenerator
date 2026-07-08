package com.pacrombie.mtgenerator.service.scoring;

import com.pacrombie.mtgenerator.model.CardEntity;
import com.pacrombie.mtgenerator.model.DeckContext;
import com.pacrombie.mtgenerator.service.scoring.rule.CardScoringRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardScoringService {

    private final List<CardScoringRule> scoringRules;

    public int score(CardEntity card, DeckContext context) {
        return scoringRules.stream()
                .mapToInt(rule -> rule.score(card, context))
                .sum();
    }
}
