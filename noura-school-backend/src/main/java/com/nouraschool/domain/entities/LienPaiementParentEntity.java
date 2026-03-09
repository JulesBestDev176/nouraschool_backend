package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lien_paiement_parent")
public class LienPaiementParentEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    public ParentEntity parent;

    @Column(nullable = false, unique = true, length = 255)
    public String token;

    @Column(name = "montant_total", nullable = false)
    public java.math.BigDecimal montantTotal;

    @Column(nullable = false, length = 7) // 2025-06
    public String mois;

    @Column(nullable = false, length = 20)
    public String statut = "EN_ATTENTE";

    @Column(name = "expires_at", nullable = false)
    public Instant expiresAt;

    @Column(name = "otp_hash")
    public String otpHash;

    @Column(name = "otp_expires_at")
    public Instant otpExpiresAt;

    @Column(name = "otp_verified")
    public Boolean otpVerified = false;
}
