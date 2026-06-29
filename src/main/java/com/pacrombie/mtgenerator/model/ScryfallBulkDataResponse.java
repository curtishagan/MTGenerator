package com.pacrombie.mtgenerator.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class ScryfallBulkDataResponse {

    private String object;

    private boolean hasMore;

    private List<ScryfallBulkData> data;
}
