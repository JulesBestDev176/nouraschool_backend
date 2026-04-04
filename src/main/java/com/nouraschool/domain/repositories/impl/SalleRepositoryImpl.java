package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.SalleEntity;
import com.nouraschool.domain.repositories.SalleRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SalleRepositoryImpl implements SalleRepository {

    @Override
    public List<SalleEntity> findByTenantId(UUID tenantId) {
        return SalleEntity.list("tenantId", tenantId);
    }

    @Override
    public List<SalleEntity> findByBatimentId(UUID batimentId) {
        return SalleEntity.list("batimentId", batimentId);
    }

    @Override
    public SalleEntity findById(UUID id) {
        return SalleEntity.findById(id);
    }

    @Override
    public SalleEntity persist(SalleEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(SalleEntity entity) {
        entity.delete();
    }
}
