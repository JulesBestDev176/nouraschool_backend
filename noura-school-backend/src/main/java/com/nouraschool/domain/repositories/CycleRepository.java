package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.CycleEntity;

import java.util.List;
import java.util.UUID;

public interface CycleRepository {

    List<CycleEntity> findByTenantId(UUID tenantId);

    CycleEntity findById(UUID id);

    CycleEntity persist(CycleEntity entity);

    void delete(CycleEntity entity);
}
