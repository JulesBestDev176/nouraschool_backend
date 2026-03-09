package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.LienPaiementParentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LienPaiementParentRepository {

    List<LienPaiementParentEntity> findByTenantId(UUID tenantId);

    LienPaiementParentEntity findById(UUID id);

    Optional<LienPaiementParentEntity> findByToken(String token);

    List<LienPaiementParentEntity> findByParentId(UUID parentId);

    LienPaiementParentEntity persist(LienPaiementParentEntity entity);

    void delete(LienPaiementParentEntity entity);
}
