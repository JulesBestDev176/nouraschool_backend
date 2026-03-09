package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.repositories.TenantRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TenantRepositoryImpl implements TenantRepository {

    private static final String DEFAULT_SLUG = "default";

    @Override
    public List<TenantEntity> findAll() {
        return TenantEntity.listAll();
    }

    @Override
    public Optional<TenantEntity> findById(UUID id) {
        return Optional.ofNullable(TenantEntity.findById(id));
    }

    @Override
    public Optional<TenantEntity> findBySlug(String slug) {
        return TenantEntity.find("slug", slug).firstResultOptional();
    }

    @Override
    public TenantEntity findDefault() {
        return findBySlug(DEFAULT_SLUG)
                .orElseThrow(() -> new IllegalStateException("Tenant default non trouvé. Exécuter les migrations Flyway."));
    }

    @Override
    public TenantEntity persist(TenantEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(TenantEntity entity) {
        entity.delete();
    }
}
