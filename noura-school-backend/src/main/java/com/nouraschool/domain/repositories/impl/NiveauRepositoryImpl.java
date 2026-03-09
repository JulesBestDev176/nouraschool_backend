package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.NiveauEntity;
import com.nouraschool.domain.repositories.NiveauRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NiveauRepositoryImpl implements NiveauRepository {

    @Override
    public List<NiveauEntity> findByTenantId(UUID tenantId) {
        return NiveauEntity.list("tenantId", tenantId);
    }

    @Override
    public List<NiveauEntity> findByCycleId(UUID cycleId) {
        return NiveauEntity.list("cycleId", cycleId);
    }

    @Override
    public NiveauEntity findById(UUID id) {
        return NiveauEntity.findById(id);
    }

    @Override
    public NiveauEntity persist(NiveauEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(NiveauEntity entity) {
        entity.delete();
    }
}
