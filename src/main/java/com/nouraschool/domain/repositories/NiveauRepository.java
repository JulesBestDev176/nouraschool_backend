package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.NiveauEntity;

import java.util.List;
import java.util.UUID;

public interface NiveauRepository {

    List<NiveauEntity> findByTenantId(UUID tenantId);

    List<NiveauEntity> findByCycleId(UUID cycleId);

    NiveauEntity findById(UUID id);

    NiveauEntity persist(NiveauEntity entity);

    void delete(NiveauEntity entity);
}
