package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.BatimentEntity;

import java.util.List;
import java.util.UUID;

public interface BatimentRepository {

    List<BatimentEntity> findByTenantId(UUID tenantId);

    BatimentEntity findById(UUID id);

    BatimentEntity persist(BatimentEntity entity);

    void delete(BatimentEntity entity);
}
