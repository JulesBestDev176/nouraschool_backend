package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.RefreshTokenEntity;
import com.nouraschool.domain.repositories.RefreshTokenRepository;
import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    @Inject
    JPAStreamer jpaStreamer;

    @Override
    public RefreshTokenEntity findByTokenHash(String tokenHash) {
        return jpaStreamer.stream(RefreshTokenEntity.class)
                .filter(t -> tokenHash.equals(t.tokenHash))
                .findFirst()
                .orElse(null);
    }

    @Override
    public RefreshTokenEntity persist(RefreshTokenEntity token) {
        token.persist();
        return token;
    }

    @Override
    public void revokeByUserId(UUID userId) {
        jpaStreamer.stream(RefreshTokenEntity.class)
                .filter(t -> userId.equals(t.user.id))
                .forEach(t -> {
                    t.revoked = true;
                    t.persist();
                });
    }
}
