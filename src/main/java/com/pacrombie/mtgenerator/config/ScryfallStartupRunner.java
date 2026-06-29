package com.pacrombie.mtgenerator.config;

import com.pacrombie.mtgenerator.service.MTGeneratorService;
import com.pacrombie.mtgenerator.service.ScryfallImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScryfallStartupRunner implements ApplicationRunner {

    private final ScryfallImportService scryfallImportService;

    @Override
    public void run(ApplicationArguments args) {
        scryfallImportService.importScryfallCards();
    }
}
