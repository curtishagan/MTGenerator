package com.pacrombie.ultimatebravery.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class CardEntity {

    @Id
    private UUID id;

    private String name;
    private String manaCost;
    private Double manaValue;
    private String typeLine;

    @Column(length = 5000)
    private String oracleText;

    private String colorIdentity;

    private Boolean commanderLegal;
}
