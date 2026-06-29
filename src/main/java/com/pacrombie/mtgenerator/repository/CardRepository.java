package com.pacrombie.mtgenerator.repository;

import com.pacrombie.mtgenerator.model.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends
        JpaRepository<CardEntity, UUID>,
        JpaSpecificationExecutor<CardEntity> {
    List<CardEntity> findByCommanderLegalTrue();
}
