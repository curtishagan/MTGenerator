package com.pacrombie.mtgenerator.downstream.scryfall;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Getter
@Validated
@ConfigurationProperties("com.pacrombie.ultimatebravery.scryfall")
public class ScryfallRestConfigProperties {

    private String baseUrl;
    //private String path1;
    //private String path2;
}
