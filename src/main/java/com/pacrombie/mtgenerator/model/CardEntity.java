package com.pacrombie.mtgenerator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class CardEntity {

    @Id
    private UUID id;

    private String oracleId;

    private String name;
    private String manaCost;
    private Double manaValue;
    private String typeLine;

    @Column(length = 5000)
    private String oracleText;

    private Set<String> colorIdentity;
    private Set<String> producedMana;

    private Integer edhrecRank;

    private Boolean commanderLegal;
    private String layout;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> oracleTags = new HashSet<>();
}
