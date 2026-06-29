package com.pacrombie.ultimatebravery.downstream.scryfall;

import com.pacrombie.ultimatebravery.model.ScryfallBulkData;
import com.pacrombie.ultimatebravery.model.ScryfallBulkDataResponse;
import com.pacrombie.ultimatebravery.model.ScryfallCardData;
import com.pacrombie.ultimatebravery.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScryfallRestClient {

    @Qualifier("scryfall-rest-client")
    private final RestClient restClient;

    @Autowired
    private final CardRepository cardRepository;

    public ScryfallCardData[] getCardData() {
        return restClient.get()
                .uri(getBulkUri())
                .retrieve()
                .body(ScryfallCardData[].class);
    }

    private String getBulkUri() {
        if (cardRepository.count() > 0) {
            throw new IllegalStateException("Repository is not empty");
        }

        ScryfallBulkDataResponse response = restClient.get()
                .uri("https://api.scryfall.com/bulk-data")
                .retrieve()
                .body(ScryfallBulkDataResponse.class);

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("No bulk data response from Scryfall");
        }

        return response.getData().stream()
                .filter(item -> "oracle_cards".equals(item.getType()))
                .findFirst()
                .map(ScryfallBulkData::getDownloadUri)
                .orElseThrow(() -> new IllegalStateException("Could not find oracle_cards bulk data"));
    }
}
