package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.LoginRequest;
import com.nouraschool.domain.dtos.LoginResponse;
import com.nouraschool.domain.entities.RefreshTokenEntity;
import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.repositories.RefreshTokenRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.AuthService;
import com.nouraschool.domain.services.JwtGenerator;
import com.nouraschool.domain.services.PasswordEncoder;
import com.nouraschool.domain.services.TokenHasher;
import com.nouraschool.runtime.aop.Logged;
import com.nouraschool.runtime.aop.Timed;
import com.nouraschool.runtime.config.ApplicationProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Logged
@Timed
public class AuthServiceImpl implements AuthService {

    private static final int REFRESH_TOKEN_DAYS = 7;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final TokenHasher tokenHasher;
    private final ApplicationProperties applicationProperties;

    @Inject
    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtGenerator jwtGenerator,
            TokenHasher tokenHasher,
            ApplicationProperties applicationProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.tokenHasher = tokenHasher;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsernameOrEmail(request.getUsername());
        if (user == null) {
            throw new InvalidRequestException("INVALID_CREDENTIALS");
        }
        if (!user.active) {
            throw new InvalidRequestException("USER_INACTIVE");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.password)) {
            throw new InvalidRequestException("INVALID_CREDENTIALS");
        }

        long lifespan = applicationProperties.jwt().accessTokenLifespan();
        String accessToken = jwtGenerator.generateAccessToken(user, Set.of(user.role.name()), lifespan);
        String refreshToken = createAndPersistRefreshToken(user);

        return LoginResponse.of(accessToken, refreshToken, lifespan);
    }

    @Override
    public LoginResponse refresh(String refreshTokenValue) {
        String tokenHash = tokenHasher.hash(refreshTokenValue);
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByTokenHash(tokenHash);
        if (refreshToken == null) {
            throw new InvalidRequestException("REFRESH_TOKEN_INVALID");
        }
        if (refreshToken.revoked) {
            throw new InvalidRequestException("REFRESH_TOKEN_INVALID");
        }
        if (refreshToken.expiresAt.isBefore(Instant.now())) {
            refreshToken.revoked = true;
            refreshTokenRepository.persist(refreshToken);
            throw new InvalidRequestException("REFRESH_TOKEN_EXPIRED");
        }

        UserEntity user = refreshToken.user;
        if (!user.active) {
            throw new InvalidRequestException("USER_INACTIVE");
        }

        long lifespan = applicationProperties.jwt().accessTokenLifespan();
        String accessToken = jwtGenerator.generateAccessToken(user, Set.of(user.role.name()), lifespan);
        return LoginResponse.of(accessToken, refreshTokenValue, lifespan);
    }

    @Override
    public void logout(UUID userId) {
        refreshTokenRepository.revokeByUserId(userId);
    }

    private String createAndPersistRefreshToken(UserEntity user) {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = tokenHasher.hash(rawToken);
        Instant expiresAt = Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS);

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.user = user;
        entity.tokenHash = tokenHash;
        entity.expiresAt = expiresAt;
        entity.revoked = false;
        entity.createdAt = LocalDateTime.now();
        refreshTokenRepository.persist(entity);

        return rawToken;
    }
}
