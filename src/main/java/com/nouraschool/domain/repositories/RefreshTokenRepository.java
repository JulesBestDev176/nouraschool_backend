package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.RefreshTokenEntity;

import java.util.UUID;

public interface RefreshTokenRepository {

    RefreshTokenEntity findByTokenHash(String tokenHash);

    RefreshTokenEntity persist(RefreshTokenEntity token);

    void revokeByUserId(UUID userId);
}
