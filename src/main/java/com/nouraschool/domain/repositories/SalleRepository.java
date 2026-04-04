package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.SalleEntity;

import java.util.List;
import java.util.UUID;

public interface SalleRepository {

    List<SalleEntity> findByTenantId(UUID tenantId);

    List<SalleEntity> findByBatimentId(UUID batimentId);

    SalleEntity findById(UUID id);

    SalleEntity persist(SalleEntity entity);

    void delete(SalleEntity entity);
}
