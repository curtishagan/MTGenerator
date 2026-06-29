package com.pacrombie.mtgenerator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class ScryfallCardData {

    private UUID id;
    private String name;

    @JsonProperty("mana_cost")
    private String manaCost;

    private Double cmc;

    @JsonProperty("type_line")
    private String typeLine;

    @JsonProperty("oracle_text")
    private String oracleText;

    @JsonProperty("color_identity")
    private Set<String> colorIdentity;

    @JsonProperty("produced_mana")
    private Set<String> producedMana;

    private Map<String, String> legalities;

}
