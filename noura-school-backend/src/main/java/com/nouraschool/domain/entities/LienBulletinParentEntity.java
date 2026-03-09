package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lien_bulletin_parent")
public class LienBulletinParentEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulletin_id", nullable = false)
    public BulletinEntity bulletin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    public ParentEntity parent;

    @Column(nullable = false, unique = true, length = 255)
    public String token;

    @Column(name = "otp_hash")
    public String otpHash;

    @Column(name = "otp_expires_at")
    public Instant otpExpiresAt;

    @Column(name = "otp_verified")
    public Boolean otpVerified = false;

    @Column(name = "expires_at", nullable = false)
    public Instant expiresAt;

    @Column(name = "ouvert_le")
    public Instant ouvertLe;
}
