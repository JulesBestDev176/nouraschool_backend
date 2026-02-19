package com.nouraschool.domain.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity extends AbstractUuidEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity user;

    @Column(name = "token_hash", nullable = false)
    public String tokenHash;

    @Column(name = "expires_at", nullable = false)
    public Instant expiresAt;

    @Column(nullable = false)
    public Boolean revoked = false;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    public static RefreshTokenEntity findByTokenHash(String tokenHash) {
        return find("tokenHash", tokenHash).firstResult();
    }

    public static List<RefreshTokenEntity> findByUserId(UUID userId) {
        return list("user.id", userId);
    }

    public static void revokeByUserId(UUID userId) {
        List<RefreshTokenEntity> tokens = findByUserId(userId);
        for (RefreshTokenEntity token : tokens) {
            token.revoked = true;
            token.persist();
        }
    }

    public static void revoke(RefreshTokenEntity token) {
        token.revoked = true;
        token.persist();
    }
}
