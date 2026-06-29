package com.pacrombie.mtgenerator.controller;

import com.pacrombie.mtgenerator.service.MTGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class UltimateBraveryRestController {

    @Autowired
    private MTGeneratorService MTGeneratorService;

    @GetMapping("deck")
    public ResponseEntity<String> getDeck() {

        return ResponseEntity.ok(MTGeneratorService.generateDeck());
    }

}
