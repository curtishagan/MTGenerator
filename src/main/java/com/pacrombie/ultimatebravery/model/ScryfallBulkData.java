package com.pacrombie.ultimatebravery.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class ScryfallBulkData {
    private String type;
    @JsonProperty("download_uri")
    private String downloadUri;
}
