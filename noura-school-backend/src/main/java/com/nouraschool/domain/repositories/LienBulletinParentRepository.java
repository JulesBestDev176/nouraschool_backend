package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.LienBulletinParentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LienBulletinParentRepository {

    List<LienBulletinParentEntity> findByTenantId(UUID tenantId);

    LienBulletinParentEntity findById(UUID id);

    Optional<LienBulletinParentEntity> findByToken(String token);

    LienBulletinParentEntity persist(LienBulletinParentEntity entity);

    void delete(LienBulletinParentEntity entity);
}
