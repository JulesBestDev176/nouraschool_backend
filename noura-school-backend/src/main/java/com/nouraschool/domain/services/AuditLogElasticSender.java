package com.nouraschool.domain.services;

import java.util.Map;
import java.util.UUID;

/**
 * Envoi des événements d'audit vers Elasticsearch (optionnel).
 * Utilisé par {@link AuditLogServiceImpl} lorsque app.logging.elastic.enabled=true.
 */
public interface AuditLogElasticSender {

    void log(String action, UUID tenantId, UUID utilisateurId, String role,
             String resourceType, UUID resourceId, Map<String, Object> details,
             String ipAddress, String userAgent);
}
