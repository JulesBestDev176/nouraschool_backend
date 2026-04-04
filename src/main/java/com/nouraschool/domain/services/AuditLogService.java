package com.nouraschool.domain.services;

import java.util.Map;
import java.util.UUID;

/**
 * Enregistrement des événements d'audit : persistance PostgreSQL (table audit_log) + envoi Elasticsearch si activé.
 * Utilisé pour LOGIN_SUCCESS, LOGOUT, etc. (injecté dans AuthServiceImpl et autres services métier).
 */
public interface AuditLogService {

    /**
     * Enregistre un événement d'audit (indexation Elasticsearch si activé).
     *
     * @param action       ex. LOGIN_SUCCESS, LOGOUT, TOKEN_REFRESH
     * @param tenantId     nullable
     * @param utilisateurId nullable
     * @param role         nullable
     * @param resourceType nullable
     * @param resourceId   nullable
     * @param details      champs additionnels (JSON)
     * @param ipAddress    nullable
     * @param userAgent    nullable
     */
    void log(String action, UUID tenantId, UUID utilisateurId, String role,
             String resourceType, UUID resourceId, Map<String, Object> details,
             String ipAddress, String userAgent);
}
