package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.TenantEntity;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {
    java.util.List<TenantEntity> findAll();

    Optional<TenantEntity> findById(UUID id);

    Optional<TenantEntity> findBySlug(String slug);

    /** Tenant par défaut (slug = default) pour seed et contexte non résolu. */
    TenantEntity findDefault();

    TenantEntity persist(TenantEntity entity);

    void delete(TenantEntity entity);
}
