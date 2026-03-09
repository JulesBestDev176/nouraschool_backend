package com.nouraschool.auth;

import com.nouraschool.domain.dtos.LoginRequest;
import com.nouraschool.domain.dtos.LoginResponse;
import com.nouraschool.domain.entities.RefreshTokenEntity;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.repositories.RefreshTokenRepository;
import com.nouraschool.domain.repositories.SurveillantCycleRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.AuditLogService;
import com.nouraschool.domain.services.JwtGenerator;
import com.nouraschool.domain.services.NotificationLogService;
import com.nouraschool.domain.services.PasswordEncoder;
import com.nouraschool.domain.services.RedisService;
import com.nouraschool.domain.services.TokenHasher;
import com.nouraschool.domain.services.impl.AuthServiceImpl;
import com.nouraschool.runtime.config.ApplicationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AuthServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    SurveillantCycleRepository surveillantCycleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtGenerator jwtGenerator;
    @Mock
    TokenHasher tokenHasher;
    @Mock
    ApplicationProperties applicationProperties;
    @Mock
    AuditLogService auditLogService;
    @Mock
    RedisService redisService;
    @Mock
    NotificationLogService notificationLogService;

    private static final String LOGIN = "admin@test.com";
    private static final String PASSWORD = "Secret123!";
    private static final long LIFESPAN = 900;

    private AuthServiceImpl authService;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        when(applicationProperties.jwt()).thenReturn(new ApplicationProperties.JwtConfig("iss", "aud", LIFESPAN));
        authService = new AuthServiceImpl(
                userRepository,
                refreshTokenRepository,
                surveillantCycleRepository,
                passwordEncoder,
                jwtGenerator,
                tokenHasher,
                applicationProperties,
                auditLogService,
                redisService,
                notificationLogService,
                5,
                600,
                3600,
                3,
                900
        );
        testUser = new UserEntity();
        testUser.id = UUID.randomUUID();
        testUser.username = "admin";
        testUser.email = LOGIN;
        testUser.password = "$2a$12$hashed";
        testUser.firstName = "Admin";
        testUser.lastName = "Test";
        testUser.telephone = "+221000000000";
        testUser.adresse = "Adresse";
        testUser.role = UserRole.ADMIN;
        testUser.active = true;
        testUser.mustChangePassword = false;
        testUser.tenant = new TenantEntity();
        testUser.tenant.id = UUID.randomUUID();
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("succès retourne tokens et audit")
        void login_success_returnsTokens() {
            when(redisService.get(anyString())).thenReturn(null);
            when(userRepository.findByUsernameOrEmailOrPhone(LOGIN)).thenReturn(testUser);
            when(passwordEncoder.matches(PASSWORD, testUser.password)).thenReturn(true);
            when(jwtGenerator.generateAccessToken(eq(testUser), any(), eq(LIFESPAN))).thenReturn("accessToken");
            when(tokenHasher.hash(anyString())).thenReturn("hash");

            LoginResponse response = authService.login(LoginRequest.builder().login(LOGIN).password(PASSWORD).build());

            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("accessToken");
            assertThat(response.getRefreshToken()).isNotNull();
            verify(redisService).delete(anyString());
            verify(auditLogService).log(eq("LOGIN_SUCCESS"), eq(testUser.tenant.id), eq(testUser.id), eq("ADMIN"), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("utilisateur inconnu lance IDENTIFIANTS_INVALIDES et incrémente lockout")
        void login_unknownUser_throwsAndIncrementsLockout() {
            when(redisService.get(anyString())).thenReturn(null);
            when(userRepository.findByUsernameOrEmailOrPhone(LOGIN)).thenReturn(null);

            assertThatThrownBy(() -> authService.login(LoginRequest.builder().login(LOGIN).password(PASSWORD).build()))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("IDENTIFIANTS_INVALIDES");
            verify(redisService).set(anyString(), eq("1"), eq(600L));
        }

        @Test
        @DisplayName("mot de passe incorrect lance IDENTIFIANTS_INVALIDES")
        void login_wrongPassword_throws() {
            when(redisService.get(anyString())).thenReturn(null);
            when(userRepository.findByUsernameOrEmailOrPhone(LOGIN)).thenReturn(testUser);
            when(passwordEncoder.matches(PASSWORD, testUser.password)).thenReturn(false);
            when(redisService.get(anyString())).thenReturn(null);

            assertThatThrownBy(() -> authService.login(LoginRequest.builder().login(LOGIN).password(PASSWORD).build()))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("IDENTIFIANTS_INVALIDES");
        }

        @Test
        @DisplayName("après 5 tentatives lance COMPTE_VERROUILLE")
        void login_after5Attempts_throwsCompteVerrouille() {
            when(redisService.get(anyString())).thenReturn("5");
            when(userRepository.findByUsernameOrEmailOrPhone(LOGIN)).thenReturn(testUser);

            assertThatThrownBy(() -> authService.login(LoginRequest.builder().login(LOGIN).password(PASSWORD).build()))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("COMPTE_VERROUILLE");
        }

        @Test
        @DisplayName("utilisateur inactif lance USER_INACTIVE")
        void login_inactiveUser_throws() {
            when(redisService.get(anyString())).thenReturn(null);
            testUser.active = false;
            when(userRepository.findByUsernameOrEmailOrPhone(LOGIN)).thenReturn(testUser);

            assertThatThrownBy(() -> authService.login(LoginRequest.builder().login(LOGIN).password(PASSWORD).build()))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("USER_INACTIVE");
        }

        @Test
        @DisplayName("tenant inactif lance TENANT_INACTIF")
        void login_tenantInactif_throws() {
            when(redisService.get(anyString())).thenReturn(null);
            testUser.tenant.actif = false;
            when(userRepository.findByUsernameOrEmailOrPhone(LOGIN)).thenReturn(testUser);
            when(passwordEncoder.matches(PASSWORD, testUser.password)).thenReturn(true);

            assertThatThrownBy(() -> authService.login(LoginRequest.builder().login(LOGIN).password(PASSWORD).build()))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("TENANT_INACTIF");
        }
    }

    @Nested
    @DisplayName("refresh")
    class Refresh {

        @Test
        @DisplayName("token valide retourne nouvel accessToken et rotation du refresh")
        void refresh_validToken_returnsNewAccessAndRotates() {
            String rawToken = "refresh-token";
            when(tokenHasher.hash(rawToken)).thenReturn("hash");
            RefreshTokenEntity refreshEntity = new RefreshTokenEntity();
            refreshEntity.user = testUser;
            refreshEntity.revoked = false;
            refreshEntity.expiresAt = Instant.now().plusSeconds(3600);
            when(refreshTokenRepository.findByTokenHash("hash")).thenReturn(refreshEntity);
            when(jwtGenerator.generateAccessToken(eq(testUser), any(), eq(LIFESPAN))).thenReturn("newAccess");
            when(tokenHasher.hash(anyString())).thenReturn("newHash");

            LoginResponse response = authService.refresh(rawToken);

            assertThat(response.getAccessToken()).isEqualTo("newAccess");
            assertThat(response.getRefreshToken()).isNotNull();
            verify(refreshTokenRepository).persist(any(RefreshTokenEntity.class));
        }

        @Test
        @DisplayName("token invalide lance REFRESH_TOKEN_INVALID")
        void refresh_invalidToken_throws() {
            when(tokenHasher.hash("bad")).thenReturn("hash");
            when(refreshTokenRepository.findByTokenHash("hash")).thenReturn(null);

            assertThatThrownBy(() -> authService.refresh("bad"))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("REFRESH_TOKEN_INVALID");
        }
    }

    @Nested
    @DisplayName("forgotPassword")
    class ForgotPassword {

        @Test
        @DisplayName("email inconnu ne lance pas et n'envoie rien")
        void forgotPassword_unknownEmail_doesNothing() {
            when(redisService.get(anyString())).thenReturn(null);
            when(userRepository.findByUsernameOrEmailOrPhone("unknown@test.com")).thenReturn(null);

            authService.forgotPassword("unknown@test.com");

            verify(notificationLogService, never()).log(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("email connu enregistre token Redis et log notification")
        void forgotPassword_knownEmail_storesTokenAndLogs() {
            when(redisService.get(anyString())).thenReturn(null);
            when(userRepository.findByUsernameOrEmailOrPhone(LOGIN)).thenReturn(testUser);

            authService.forgotPassword(LOGIN);

            verify(redisService).set(startsWith("auth:reset:"), eq(testUser.id.toString()), eq(3600L));
            verify(notificationLogService).log(eq(testUser.tenant.id), eq("EMAIL"), eq(LOGIN), any(), any());
        }

        @Test
        @DisplayName("trop de demandes lance TOO_MANY_REQUESTS")
        void forgotPassword_tooManyRequests_throws() {
            when(redisService.get(anyString())).thenReturn("3");
            when(userRepository.findByUsernameOrEmailOrPhone(LOGIN)).thenReturn(testUser);

            assertThatThrownBy(() -> authService.forgotPassword(LOGIN))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("TOO_MANY_REQUESTS");
        }
    }

    @Nested
    @DisplayName("resetPassword")
    class ResetPassword {

        @Test
        @DisplayName("token invalide lance RESET_TOKEN_INVALIDE")
        void resetPassword_invalidToken_throws() {
            when(redisService.get("auth:reset:bad-token")).thenReturn(null);

            assertThatThrownBy(() -> authService.resetPassword("bad-token", "NewPass123!x"))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("RESET_TOKEN_INVALIDE");
        }

        @Test
        @DisplayName("token valide met à jour le mot de passe et supprime le token")
        void resetPassword_validToken_updatesPassword() {
            String token = "valid-token";
            when(redisService.get("auth:reset:" + token)).thenReturn(testUser.id.toString());
            when(userRepository.findById(testUser.id)).thenReturn(testUser);
            String newPwd = "NewPass123!x";
            when(passwordEncoder.encode(newPwd)).thenReturn("$2a$12$newHashed");

            authService.resetPassword(token, newPwd);

            verify(redisService).delete("auth:reset:" + token);
            assertThat(testUser.password).isEqualTo("$2a$12$newHashed");
            assertThat(testUser.mustChangePassword).isFalse();
            verify(userRepository).persist(testUser);
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        @Test
        @DisplayName("ancien mot de passe incorrect lance MOT_DE_PASSE_ACTUEL_INCORRECT")
        void changePassword_wrongOld_throws() {
            when(userRepository.findById(testUser.id)).thenReturn(testUser);
            when(passwordEncoder.matches("wrong", testUser.password)).thenReturn(false);

            assertThatThrownBy(() -> authService.changePassword(testUser.id, "wrong", "NewPass123!x"))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage("MOT_DE_PASSE_ACTUEL_INCORRECT");
        }

        @Test
        @DisplayName("ancien correct met à jour et révoque les refresh tokens")
        void changePassword_ok_updatesAndRevokes() {
            when(userRepository.findById(testUser.id)).thenReturn(testUser);
            String newPwd = "NewPass123!x";
            when(passwordEncoder.matches("OldPass123!x", testUser.password)).thenReturn(true);
            when(passwordEncoder.encode(newPwd)).thenReturn("$2a$12$newHashed");

            authService.changePassword(testUser.id, "OldPass123!x", newPwd);

            assertThat(testUser.password).isEqualTo("$2a$12$newHashed");
            assertThat(testUser.mustChangePassword).isFalse();
            verify(refreshTokenRepository).revokeByUserId(testUser.id);
        }
    }
}
