package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.AuditLogEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditLogRepository {

    List<AuditLogEntity> findAll(int page, int size);

    List<AuditLogEntity> findByTenantId(UUID tenantId, int page, int size);

    List<AuditLogEntity> findByUtilisateurId(UUID utilisateurId, int page, int size);

    Optional<AuditLogEntity> findById(UUID id);

    long count();

    long countByTenantId(UUID tenantId);

    long countByUtilisateurId(UUID utilisateurId);
}
