package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.LienBulletinParentEntity;
import com.nouraschool.domain.repositories.LienBulletinParentRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class LienBulletinParentRepositoryImpl implements LienBulletinParentRepository {

    @Override
    public List<LienBulletinParentEntity> findByTenantId(UUID tenantId) {
        return LienBulletinParentEntity.list("tenantId", tenantId);
    }

    @Override
    public LienBulletinParentEntity findById(UUID id) {
        return LienBulletinParentEntity.findById(id);
    }

    @Override
    public Optional<LienBulletinParentEntity> findByToken(String token) {
        return LienBulletinParentEntity.find("token", token).firstResultOptional();
    }

    @Override
    public LienBulletinParentEntity persist(LienBulletinParentEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(LienBulletinParentEntity entity) {
        entity.delete();
    }
}
