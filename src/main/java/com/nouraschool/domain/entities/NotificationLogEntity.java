package com.nouraschool.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Log de chaque envoi de notification (task.md section 15 — notification_log).
 * Enregistré avant envoi (WHATSAPP, SMS, EMAIL, IN_APP) pour traçabilité et retentatives.
 */
@Entity
@Table(name = "notification_log")
public class NotificationLogEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    public UUID id;

    @Column(name = "tenant_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    public UUID tenantId;

    /** WHATSAPP | SMS | EMAIL | IN_APP */
    @Column(nullable = false, length = 20)
    public String canal;

    /** Email ou numéro de téléphone du destinataire */
    @Column(length = 254)
    public String destinataire;

    @Column(length = 255)
    public String sujet;

    @Column(columnDefinition = "TEXT")
    public String contenu;

    /** EN_ATTENTE | ENVOYE | ECHEC */
    @Column(nullable = false, length = 20)
    public String statut = "EN_ATTENTE";

    @Column(columnDefinition = "TEXT")
    public String erreur;

    @Column(name = "envoye_le")
    public Instant envoyeLe;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();
}
