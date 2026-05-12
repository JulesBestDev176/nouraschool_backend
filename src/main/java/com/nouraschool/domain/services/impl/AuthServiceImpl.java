package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.AuthMeDto;
import com.nouraschool.domain.dtos.LoginRequest;
import com.nouraschool.domain.dtos.LoginResponse;
import com.nouraschool.domain.entities.RefreshTokenEntity;
import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.constants.Constants;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.repositories.PlateformeUtilisateurRepository;
import com.nouraschool.domain.repositories.RefreshTokenRepository;
import com.nouraschool.domain.repositories.SurveillantCycleRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.AuditLogService;
import com.nouraschool.domain.services.AuthService;
import com.nouraschool.domain.services.JwtGenerator;
import com.nouraschool.domain.services.NotificationLogService;
import com.nouraschool.domain.services.PasswordEncoder;
import com.nouraschool.domain.services.RedisService;
import com.nouraschool.domain.services.TokenHasher;
import com.nouraschool.runtime.aop.Logged;
import com.nouraschool.runtime.aop.Timed;
import com.nouraschool.runtime.config.ApplicationProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Logged
@Timed
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = Logger.getLogger(AuthServiceImpl.class);
    private static final int REFRESH_TOKEN_DAYS = 7;
    private static final String REDIS_KEY_LOCKOUT = "auth:lockout:";
    private static final String REDIS_KEY_RESET = "auth:reset:";
    private static final String REDIS_KEY_RATELIMIT_FORGOT = "auth:ratelimit:forgot:";

    private final UserRepository userRepository;
    private final PlateformeUtilisateurRepository plateformeUtilisateurRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SurveillantCycleRepository surveillantCycleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final TokenHasher tokenHasher;
    private final ApplicationProperties applicationProperties;
    private final AuditLogService auditLogService;
    private final RedisService redisService;
    private final NotificationLogService notificationLogService;
    private final int lockoutMaxAttempts;
    private final long lockoutTtlSeconds;
    private final long resetPasswordTokenTtlSeconds;
    private final int forgotPerEmail;
    private final long forgotWindowSeconds;

    @Inject
    public AuthServiceImpl(
            UserRepository userRepository,
            PlateformeUtilisateurRepository plateformeUtilisateurRepository,
            RefreshTokenRepository refreshTokenRepository,
            SurveillantCycleRepository surveillantCycleRepository,
            PasswordEncoder passwordEncoder,
            JwtGenerator jwtGenerator,
            TokenHasher tokenHasher,
            ApplicationProperties applicationProperties,
            AuditLogService auditLogService,
            RedisService redisService,
            NotificationLogService notificationLogService,
            @ConfigProperty(name = "app.auth.lockout.max-attempts", defaultValue = "5") int lockoutMaxAttempts,
            @ConfigProperty(name = "app.auth.lockout.ttl-seconds", defaultValue = "600") long lockoutTtlSeconds,
            @ConfigProperty(name = "app.auth.reset-password.token-ttl-seconds", defaultValue = "3600") long resetPasswordTokenTtlSeconds,
            @ConfigProperty(name = "app.auth.rate-limit.forgot-per-email", defaultValue = "3") int forgotPerEmail,
            @ConfigProperty(name = "app.auth.rate-limit.forgot-window-seconds", defaultValue = "900") long forgotWindowSeconds) {
        this.userRepository = userRepository;
        this.plateformeUtilisateurRepository = plateformeUtilisateurRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.surveillantCycleRepository = surveillantCycleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.tokenHasher = tokenHasher;
        this.applicationProperties = applicationProperties;
        this.auditLogService = auditLogService;
        this.redisService = redisService;
        this.notificationLogService = notificationLogService;
        this.lockoutMaxAttempts = lockoutMaxAttempts;
        this.lockoutTtlSeconds = lockoutTtlSeconds;
        this.resetPasswordTokenTtlSeconds = resetPasswordTokenTtlSeconds;
        this.forgotPerEmail = forgotPerEmail;
        this.forgotWindowSeconds = forgotWindowSeconds;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String normalizedLogin = normalizeLogin(request.getLogin());
        String lockoutKey = REDIS_KEY_LOCKOUT + normalizeLockoutKey(request.getLogin());
        int attempts = getLockoutAttempts(lockoutKey);
        LOG.infov("Login attempt received login={0} lockoutAttempts={1}", maskLogin(normalizedLogin), attempts);
        if (attempts >= lockoutMaxAttempts) {
            LOG.warnv("Login rejected reason=COMPTE_VERROUILLE login={0} attempts={1}", maskLogin(normalizedLogin), attempts);
            throw new InvalidRequestException("COMPTE_VERROUILLE");
        }
        checkExponentialDelay(lockoutKey, attempts);

        UserEntity user = userRepository.findByUsernameOrEmailOrPhone(request.getLogin());
        if (user == null) {
            return loginPlatformUser(request, lockoutKey, normalizedLogin);
        }
        if (!user.active) {
            LOG.warnv("Tenant login rejected reason=USER_INACTIVE userId={0} login={1} role={2}",
                    user.id,
                    maskLogin(normalizedLogin),
                    user.role);
            throw new InvalidRequestException("USER_INACTIVE");
        }
        if (user.tenant != null && !Boolean.TRUE.equals(user.tenant.actif)) {
            LOG.warnv("Tenant login rejected reason=TENANT_INACTIF userId={0} tenantId={1} login={2}",
                    user.id,
                    user.tenant.id,
                    maskLogin(normalizedLogin));
            throw new InvalidRequestException("TENANT_INACTIF");
        }
        if (user.role == UserRole.PARENT) {
            LOG.warnv("Tenant login rejected reason=PARENT_LOGIN_DISABLED userId={0} tenantId={1} login={2}",
                    user.id,
                    user.tenant != null ? user.tenant.id : null,
                    maskLogin(normalizedLogin));
            throw new InvalidRequestException("PARENT_LOGIN_DISABLED");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.password)) {
            int newAttempts = incrementLockout(lockoutKey);
            LOG.warnv("Tenant login failed reason=INVALID_PASSWORD userId={0} tenantId={1} login={2} attempts={3}",
                    user.id,
                    user.tenant != null ? user.tenant.id : null,
                    maskLogin(normalizedLogin),
                    newAttempts);
            if (newAttempts >= lockoutMaxAttempts) {
                LOG.warnv("Tenant login rejected reason=COMPTE_VERROUILLE userId={0} login={1} attempts={2}",
                        user.id,
                        maskLogin(normalizedLogin),
                        newAttempts);
                throw new InvalidRequestException("COMPTE_VERROUILLE");
            }
            throw new InvalidRequestException("IDENTIFIANTS_INVALIDES");
        }

        redisService.delete(lockoutKey);

        long lifespan = applicationProperties.jwt().accessTokenLifespan();
        String accessToken = jwtGenerator.generateAccessToken(user, Set.of(user.role.name()), lifespan);
        String refreshToken = createAndPersistRefreshToken(user);
        boolean mustChange = user.mustChangePassword != null && user.mustChangePassword;

        auditLogService.log("LOGIN_SUCCESS", user.tenant != null ? user.tenant.id : null, user.id, user.role != null ? user.role.name() : null, null, null, null, null, null);
        LOG.infov("Tenant login succeeded userId={0} tenantId={1} role={2} mustChangePassword={3}",
                user.id,
                user.tenant != null ? user.tenant.id : null,
                user.role,
                mustChange);

        return LoginResponse.of(accessToken, refreshToken, lifespan, mustChange);
    }

    private LoginResponse loginPlatformUser(LoginRequest request, String lockoutKey, String normalizedLogin) {
        var platformUser = plateformeUtilisateurRepository.findByEmail(normalizedLogin).orElse(null);
        if (platformUser == null) {
            int newAttempts = incrementLockout(lockoutKey);
            LOG.warnv("Login failed reason=UNKNOWN_ACCOUNT login={0} attempts={1}", maskLogin(normalizedLogin), newAttempts);
            throw new InvalidRequestException("IDENTIFIANTS_INVALIDES");
        }
        if (!Boolean.TRUE.equals(platformUser.actif)) {
            LOG.warnv("Platform login rejected reason=USER_INACTIVE userId={0} login={1} role={2}",
                    platformUser.id,
                    maskLogin(normalizedLogin),
                    platformUser.rolePlateforme);
            throw new InvalidRequestException("USER_INACTIVE");
        }
        if (!passwordEncoder.matches(request.getPassword(), platformUser.motDePasse)) {
            int newAttempts = incrementLockout(lockoutKey);
            LOG.warnv("Platform login failed reason=INVALID_PASSWORD userId={0} login={1} role={2} attempts={3}",
                    platformUser.id,
                    maskLogin(normalizedLogin),
                    platformUser.rolePlateforme,
                    newAttempts);
            if (newAttempts >= lockoutMaxAttempts) {
                LOG.warnv("Platform login rejected reason=COMPTE_VERROUILLE userId={0} login={1} attempts={2}",
                        platformUser.id,
                        maskLogin(normalizedLogin),
                        newAttempts);
                throw new InvalidRequestException("COMPTE_VERROUILLE");
            }
            throw new InvalidRequestException("IDENTIFIANTS_INVALIDES");
        }

        redisService.delete(lockoutKey);

        long lifespan = applicationProperties.jwt().accessTokenLifespan();
        String accessToken = jwtGenerator.generatePlatformAccessToken(
                platformUser,
                Set.of(platformUser.rolePlateforme),
                lifespan
        );
        auditLogService.log("PLATFORM_LOGIN_SUCCESS", null, platformUser.id, platformUser.rolePlateforme, null, null, null, null, null);
        LOG.infov("Platform login succeeded userId={0} role={1}", platformUser.id, platformUser.rolePlateforme);
        return LoginResponse.of(accessToken, null, lifespan, false);
    }

    private static String normalizeLogin(String login) {
        return login != null ? login.trim().toLowerCase() : "";
    }

    private static String maskLogin(String login) {
        if (login == null || login.isBlank()) {
            return "blank";
        }
        int at = login.indexOf('@');
        if (at > 1) {
            return login.charAt(0) + "***" + login.substring(at);
        }
        if (login.length() <= 3) {
            return "***";
        }
        return login.charAt(0) + "***" + login.charAt(login.length() - 1);
    }

    private static String normalizeLockoutKey(String login) {
        return (login != null ? login.trim().toLowerCase() : "").replaceAll("[^a-z0-9@.+\\-]", "_");
    }

    private int getCount(String key) {
        String v = redisService.get(key);
        if (v == null || v.isBlank()) return 0;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void incrementCount(String key, long ttlSeconds) {
        int current = getCount(key);
        redisService.set(key, String.valueOf(current + 1), ttlSeconds);
    }

    private int getLockoutAttempts(String lockoutKey) {
        String v = redisService.get(lockoutKey);
        if (v == null || v.isBlank()) return 0;
        int colon = v.indexOf(':');
        String countPart = colon >= 0 ? v.substring(0, colon).trim() : v.trim();
        try {
            return Integer.parseInt(countPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Délai exponentiel : si tentatives > 0, exige 2^attempts secondes (max 120) depuis la dernière tentative. */
    private void checkExponentialDelay(String lockoutKey, int attempts) {
        if (attempts <= 0) return;
        String v = redisService.get(lockoutKey);
        if (v == null || v.isBlank()) return;
        int colon = v.indexOf(':');
        if (colon < 0) return;
        try {
            long lastEpoch = Long.parseLong(v.substring(colon + 1).trim());
            long delaySeconds = Math.min(1L << Math.min(attempts, 7), 120L);
            if (Instant.now().getEpochSecond() - lastEpoch < delaySeconds) {
                throw new InvalidRequestException("COMPTE_VERROUILLE");
            }
        } catch (NumberFormatException ignored) {
            // ignore malformed value
        }
    }

    /** Incrémente le compteur de tentatives (format "count:lastEpochSec") et retourne la nouvelle valeur. */
    private int incrementLockout(String lockoutKey) {
        int current = getLockoutAttempts(lockoutKey);
        int next = current + 1;
        long now = Instant.now().getEpochSecond();
        redisService.set(lockoutKey, next + ":" + now, lockoutTtlSeconds);
        return next;
    }

    @Override
    @Transactional
    public LoginResponse refresh(String refreshTokenValue) {
        String tokenHash = tokenHasher.hash(refreshTokenValue);
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByTokenHash(tokenHash);
        if (refreshToken == null) {
            throw new InvalidRequestException("REFRESH_TOKEN_INVALID");
        }
        if (refreshToken.revoked) {
            refreshTokenRepository.revokeByUserId(refreshToken.user.id);
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

        refreshToken.revoked = true;
        refreshTokenRepository.persist(refreshToken);
        String newRefreshToken = createAndPersistRefreshToken(user);

        long lifespan = applicationProperties.jwt().accessTokenLifespan();
        String accessToken = jwtGenerator.generateAccessToken(user, Set.of(user.role.name()), lifespan);
        boolean mustChange = user.mustChangePassword != null && user.mustChangePassword;
        return LoginResponse.of(accessToken, newRefreshToken, lifespan, mustChange);
    }

    @Override
    @Transactional
    public AuthMeDto me(SecurityContext securityContext) {
        UUID userId = com.nouraschool.runtime.security.CurrentUser.getUserId(securityContext)
                .orElseThrow(() -> new InvalidRequestException("ACCES_REFUSE"));
        UserEntity user = userRepository.findById(userId);
        if (user == null) {
            throw new InvalidRequestException("RESSOURCE_INTROUVABLE");
        }
        String tenantId = user.tenant != null ? user.tenant.id.toString() : null;
        List<String> cycles = user.role == UserRole.SURVEILLANT
                ? surveillantCycleRepository.findCycleCodesBySurveillantId(userId)
                : Collections.emptyList();
        return AuthMeDto.builder()
                .id(user.id)
                .nom(user.lastName)
                .prenom(user.firstName)
                .email(user.email)
                .role(user.role != null ? user.role.name() : null)
                .tenantId(tenantId)
                .cycles(cycles)
                .actif(user.active)
                .build();
    }

    @Override
    public void logout(UUID userId) {
        refreshTokenRepository.revokeByUserId(userId);
        auditLogService.log("LOGOUT", null, userId, null, null, null, null, null, null);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        if (email == null || email.isBlank()) return;
        String normalizedEmail = email.trim().toLowerCase();
        String rateLimitKey = REDIS_KEY_RATELIMIT_FORGOT + normalizedEmail.replaceAll("[^a-z0-9@.+\\-]", "_");
        int count = getCount(rateLimitKey);
        if (count >= forgotPerEmail) {
            throw new InvalidRequestException("TOO_MANY_REQUESTS");
        }
        incrementCount(rateLimitKey, forgotWindowSeconds);

        UserEntity user = userRepository.findByUsernameOrEmailOrPhone(email.trim());
        if (user == null) return;
        String token = UUID.randomUUID().toString();
        redisService.set(REDIS_KEY_RESET + token, user.id.toString(), resetPasswordTokenTtlSeconds);
        notificationLogService.log(
                user.tenant != null ? user.tenant.id : null,
                NotificationLogService.CANAL_EMAIL,
                user.email,
                "Réinitialisation du mot de passe",
                "Token: " + token
        );
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new InvalidRequestException("RESET_TOKEN_INVALIDE");
        }
        if (newPassword.length() < Constants.PASSWORD_MIN_LENGTH) {
            throw new InvalidRequestException("MOT_DE_PASSE_TROP_COURT");
        }
        String key = REDIS_KEY_RESET + token.trim();
        String userIdStr = redisService.get(key);
        if (userIdStr == null || userIdStr.isBlank()) {
            throw new InvalidRequestException("RESET_TOKEN_INVALIDE");
        }
        redisService.delete(key);
        UUID userId = UUID.fromString(userIdStr);
        UserEntity user = userRepository.findById(userId);
        if (user == null) {
            throw new InvalidRequestException("RESET_TOKEN_INVALIDE");
        }
        user.password = passwordEncoder.encode(newPassword);
        user.mustChangePassword = false;
        userRepository.persist(user);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        UserEntity user = userRepository.findById(userId);
        if (user == null) {
            throw new InvalidRequestException("RESSOURCE_INTROUVABLE");
        }
        if (!passwordEncoder.matches(oldPassword, user.password)) {
            throw new InvalidRequestException("MOT_DE_PASSE_ACTUEL_INCORRECT");
        }
        if (newPassword.length() < Constants.PASSWORD_MIN_LENGTH) {
            throw new InvalidRequestException("MOT_DE_PASSE_TROP_COURT");
        }
        user.password = passwordEncoder.encode(newPassword);
        user.mustChangePassword = false;
        userRepository.persist(user);
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
