package com.pacrombie.mtgenerator.model;

import lombok.Data;

import java.util.List;

@Data
public class ScryfallOracleTagData {
    private String slug;
    private List<ScryfallTaggingData> taggings;
}
