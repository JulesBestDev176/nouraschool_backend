package com.nouraschool.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Entité pour la table audit_log (migration V8).
 * Persistance systématique des événements d'audit ; envoi Elastic optionnel via AuditLogServiceImpl.
 */
@Entity
@Table(name = "audit_log")
public class AuditLogEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    public UUID id;

    @Column(name = "tenant_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    public UUID tenantId;

    @Column(name = "utilisateur_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    public UUID utilisateurId;

    @Column(length = 50)
    public String role;

    @Column(nullable = false, length = 100)
    public String action;

    @Column(name = "resource_type", length = 100)
    public String resourceType;

    @Column(name = "resource_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    public UUID resourceId;

    @JdbcTypeCode(SqlTypes.JSON)
    public Map<String, Object> details;

    @Column(name = "ip_address", length = 45)
    public String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    public String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();
}
