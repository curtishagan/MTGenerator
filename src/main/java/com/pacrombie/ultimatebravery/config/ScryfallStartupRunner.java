package com.pacrombie.ultimatebravery.config;

import com.pacrombie.ultimatebravery.service.UltimateBraveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScryfallStartupRunner implements ApplicationRunner {

    private final UltimateBraveryService ultimateBraveryService;

    @Override
    public void run(ApplicationArguments args) {
        ultimateBraveryService.importScryfallCards();
    }
}
