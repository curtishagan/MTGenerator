package com.pacrombie.mtgenerator.downstream.scryfall;

import com.pacrombie.mtgenerator.model.ScryfallBulkData;
import com.pacrombie.mtgenerator.model.ScryfallBulkDataResponse;
import com.pacrombie.mtgenerator.model.ScryfallCardData;
import com.pacrombie.mtgenerator.model.ScryfallOracleTagData;
import com.pacrombie.mtgenerator.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScryfallRestClient {

    @Qualifier("scryfall-rest-client")
    private final RestClient restClient;
    private final CardRepository cardRepository;

    public ScryfallCardData[] getCardData() {
        return restClient.get()
                .uri(getBulkUri("oracle_cards"))
                .retrieve()
                .body(ScryfallCardData[].class);
    }

    public ScryfallOracleTagData[] getOracleTagData() {
        return restClient.get()
                .uri(getBulkUri("oracle_tags"))
                .retrieve()
                .body(ScryfallOracleTagData[].class);
    }

    private String getBulkUri(String uriId) {
        ScryfallBulkDataResponse response = restClient.get()
                .uri("https://api.scryfall.com/bulk-data")
                .retrieve()
                .body(ScryfallBulkDataResponse.class);

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("No bulk data response from Scryfall");
        }

        return response.getData().stream()
                .filter(item -> uriId.equals(item.getType()))
                .findFirst()
                .map(ScryfallBulkData::getDownloadUri)
                .orElseThrow(() -> new IllegalStateException("Could not find " + uriId + " bulk data"));
    }
}
