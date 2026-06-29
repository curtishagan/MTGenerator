package com.pacrombie.mtgenerator.downstream.scryfall;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class ScryfallRestClientProvider {

    @Bean("scryfall-rest-client")
    public RestClient scryfallRestClient(){
        return RestClient.builder()
                .defaultHeader("User-Agent", "Pacrombie/1.0")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
