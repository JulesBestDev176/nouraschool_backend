package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.AuditLogEntity;
import com.nouraschool.domain.repositories.AuditLogRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private static final Sort DEFAULT_SORT = Sort.descending("createdAt");

    @Override
    public List<AuditLogEntity> findAll(int page, int size) {
        return AuditLogEntity.findAll(DEFAULT_SORT).page(Page.of(page, size)).list();
    }

    @Override
    public List<AuditLogEntity> findByTenantId(UUID tenantId, int page, int size) {
        return AuditLogEntity.find("tenantId = ?1", Sort.descending("createdAt"), tenantId)
                .page(Page.of(page, size)).list();
    }

    @Override
    public List<AuditLogEntity> findByUtilisateurId(UUID utilisateurId, int page, int size) {
        return AuditLogEntity.find("utilisateurId = ?1", Sort.descending("createdAt"), utilisateurId)
                .page(Page.of(page, size)).list();
    }

    @Override
    public Optional<AuditLogEntity> findById(UUID id) {
        return Optional.ofNullable(AuditLogEntity.findById(id));
    }

    @Override
    public long count() {
        return AuditLogEntity.count();
    }

    @Override
    public long countByTenantId(UUID tenantId) {
        return AuditLogEntity.count("tenantId", tenantId);
    }

    @Override
    public long countByUtilisateurId(UUID utilisateurId) {
        return AuditLogEntity.count("utilisateurId", utilisateurId);
    }
}
