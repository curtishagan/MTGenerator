package com.pacrombie.mtgenerator.controller;

import com.pacrombie.mtgenerator.model.DeckRequest;
import com.pacrombie.mtgenerator.service.MTGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class MTGeneratorRestController {

    @Autowired
    private MTGeneratorService MTGeneratorService;

    @PostMapping("deck")
    public ResponseEntity<String> getDeck(@RequestBody DeckRequest deckRequest) {

        log.info(deckRequest.toString());
        return ResponseEntity.ok(MTGeneratorService.generateDeck(deckRequest));
    }

}
