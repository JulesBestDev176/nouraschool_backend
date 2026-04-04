package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.PointageEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PointageRepository {

    List<PointageEntity> findByTenantId(UUID tenantId);

    List<PointageEntity> findByUtilisateurId(UUID utilisateurId);

    List<PointageEntity> findByDateRange(UUID tenantId, Instant debut, Instant fin);

    PointageEntity findById(UUID id);

    PointageEntity persist(PointageEntity entity);

    void delete(PointageEntity entity);
}
