package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.AnneeAcademiqueEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnneeAcademiqueRepository {

    List<AnneeAcademiqueEntity> findByTenantId(UUID tenantId);

    Optional<AnneeAcademiqueEntity> findCouranteByTenantId(UUID tenantId);

    AnneeAcademiqueEntity findById(UUID id);

    AnneeAcademiqueEntity persist(AnneeAcademiqueEntity entity);

    void delete(AnneeAcademiqueEntity entity);
}
