package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.LienPaiementParentEntity;
import com.nouraschool.domain.repositories.LienPaiementParentRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class LienPaiementParentRepositoryImpl implements LienPaiementParentRepository {

    @Override
    public List<LienPaiementParentEntity> findByTenantId(UUID tenantId) {
        return LienPaiementParentEntity.list("tenantId", tenantId);
    }

    @Override
    public LienPaiementParentEntity findById(UUID id) {
        return LienPaiementParentEntity.findById(id);
    }

    @Override
    public Optional<LienPaiementParentEntity> findByToken(String token) {
        return LienPaiementParentEntity.find("token", token).firstResultOptional();
    }

    @Override
    public List<LienPaiementParentEntity> findByParentId(UUID parentId) {
        return LienPaiementParentEntity.list("parent.id", parentId);
    }

    @Override
    public LienPaiementParentEntity persist(LienPaiementParentEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(LienPaiementParentEntity entity) {
        entity.delete();
    }
}
