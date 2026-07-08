package com.pacrombie.mtgenerator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ScryfallTaggingData {

    @JsonProperty("oracle_id")
    private String oracleId;

    private String weight;
}
