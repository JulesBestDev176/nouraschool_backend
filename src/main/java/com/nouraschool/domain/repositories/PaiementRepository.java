package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.PaiementEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaiementRepository {

    List<PaiementEntity> findAll();

    List<PaiementEntity> findByEleveId(UUID eleveId);

    List<PaiementEntity> findByParentId(UUID parentId);

    List<PaiementEntity> findBetweenDates(LocalDateTime debut, LocalDateTime fin);

    PaiementEntity findById(UUID id);

    PaiementEntity persist(PaiementEntity entity);

    void delete(PaiementEntity entity);
}
