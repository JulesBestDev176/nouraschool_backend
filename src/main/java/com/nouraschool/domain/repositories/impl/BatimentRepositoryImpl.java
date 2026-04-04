package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.BatimentEntity;
import com.nouraschool.domain.repositories.BatimentRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BatimentRepositoryImpl implements BatimentRepository {

    @Override
    public List<BatimentEntity> findByTenantId(UUID tenantId) {
        return BatimentEntity.list("tenantId", tenantId);
    }

    @Override
    public BatimentEntity findById(UUID id) {
        return BatimentEntity.findById(id);
    }

    @Override
    public BatimentEntity persist(BatimentEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(BatimentEntity entity) {
        entity.delete();
    }
}
