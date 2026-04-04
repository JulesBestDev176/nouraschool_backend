package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.AnneeAcademiqueEntity;
import com.nouraschool.domain.repositories.AnneeAcademiqueRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AnneeAcademiqueRepositoryImpl implements AnneeAcademiqueRepository {

    @Override
    public List<AnneeAcademiqueEntity> findByTenantId(UUID tenantId) {
        return AnneeAcademiqueEntity.list("tenantId", tenantId);
    }

    @Override
    public Optional<AnneeAcademiqueEntity> findCouranteByTenantId(UUID tenantId) {
        return AnneeAcademiqueEntity.find("tenantId = ?1 and estCourante = true", tenantId).firstResultOptional();
    }

    @Override
    public AnneeAcademiqueEntity findById(UUID id) {
        return AnneeAcademiqueEntity.findById(id);
    }

    @Override
    public AnneeAcademiqueEntity persist(AnneeAcademiqueEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(AnneeAcademiqueEntity entity) {
        entity.delete();
    }
}
