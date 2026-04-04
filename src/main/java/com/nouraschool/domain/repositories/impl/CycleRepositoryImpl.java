package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.CycleEntity;
import com.nouraschool.domain.repositories.CycleRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CycleRepositoryImpl implements CycleRepository {

    @Override
    public List<CycleEntity> findByTenantId(UUID tenantId) {
        return CycleEntity.list("tenantId", tenantId);
    }

    @Override
    public CycleEntity findById(UUID id) {
        return CycleEntity.findById(id);
    }

    @Override
    public CycleEntity persist(CycleEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(CycleEntity entity) {
        entity.delete();
    }
}
