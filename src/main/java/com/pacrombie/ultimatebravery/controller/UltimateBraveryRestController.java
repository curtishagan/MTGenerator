package com.pacrombie.ultimatebravery.controller;

import com.pacrombie.ultimatebravery.service.UltimateBraveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pacrombie.ultimatebravery.model.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class UltimateBraveryRestController {

    @Autowired
    private UltimateBraveryService ultimateBraveryService;

    @GetMapping("deck")
    public ResponseEntity<String> getDeck() {

        return ResponseEntity.ok(ultimateBraveryService.generateDeck());
    }

}
