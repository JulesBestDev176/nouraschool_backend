package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.entities.AuditLogEntity;
import com.nouraschool.domain.services.AuditLogElasticSender;
import com.nouraschool.domain.services.AuditLogService;
import com.nouraschool.runtime.config.AppConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Implémentation principale : persistance en PostgreSQL (audit_log) + envoi Elasticsearch si activé.
 */
@ApplicationScoped
public class AuditLogServiceImpl implements AuditLogService {

    @Inject
    AppConfigMapping appConfig;

    @Inject
    AuditLogElasticSender elasticSender;

    @Override
    @Transactional
    public void log(String action, UUID tenantId, UUID utilisateurId, String role,
                    String resourceType, UUID resourceId, Map<String, Object> details,
                    String ipAddress, String userAgent) {
        persistToDb(action, tenantId, utilisateurId, role, resourceType, resourceId, details, ipAddress, userAgent);
        if (appConfig.isElasticEnabled()) {
            elasticSender.log(action, tenantId, utilisateurId, role, resourceType, resourceId, details, ipAddress, userAgent);
        }
    }

    private void persistToDb(String action, UUID tenantId, UUID utilisateurId, String role,
                             String resourceType, UUID resourceId, Map<String, Object> details,
                             String ipAddress, String userAgent) {
        var entity = new AuditLogEntity();
        entity.action = action;
        entity.tenantId = tenantId;
        entity.utilisateurId = utilisateurId;
        entity.role = role;
        entity.resourceType = resourceType;
        entity.resourceId = resourceId;
        entity.details = details;
        entity.ipAddress = ipAddress;
        entity.userAgent = userAgent;
        entity.persist();
    }
}
