package com.nouraschool.auth;

import com.nouraschool.domain.entities.AdministrateurEntity;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.services.JwtGenerator;
import com.nouraschool.domain.services.JwtService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour JwtService (validation et parsing de tokens).
 * Utilise un JWT généré par JwtGenerator pour tester le flux réel.
 */
@QuarkusTest
class JwtServiceTest {

    @Inject
    JwtService jwtService;

    @Inject
    JwtGenerator jwtGenerator;

    @Test
    @DisplayName("isValid retourne true pour un token généré par JwtGenerator")
    void isValid_validToken_returnsTrue() {
        String token = generateValidToken();
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    @DisplayName("isValid retourne false pour une chaîne vide")
    void isValid_blank_returnsFalse() {
        assertThat(jwtService.isValid("")).isFalse();
        assertThat(jwtService.isValid("   ")).isFalse();
        assertThat(jwtService.isValid(null)).isFalse();
    }

    @Test
    @DisplayName("isValid retourne false pour un token invalide")
    void isValid_invalidToken_returnsFalse() {
        assertThat(jwtService.isValid("not.a.jwt")).isFalse();
        assertThat(jwtService.isValid("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.bad.signature")).isFalse();
    }

    @Test
    @DisplayName("parse retourne un JsonWebToken pour un token valide")
    void parse_validToken_returnsJwt() {
        String token = generateValidToken();
        assertThat(jwtService.parse(token)).isPresent();
        assertThat(jwtService.parse(token).get().getClaim("userId")).isNotNull();
    }

    @Test
    @DisplayName("parse retourne empty pour un token invalide")
    void parse_invalidToken_returnsEmpty() {
        assertThat(jwtService.parse("invalid")).isEmpty();
        assertThat(jwtService.parse(null)).isEmpty();
        assertThat(jwtService.parse("")).isEmpty();
    }

    private String generateValidToken() {
        AdministrateurEntity user = new AdministrateurEntity();
        user.id = java.util.UUID.randomUUID();
        user.username = "jwt-test-user";
        user.email = "jwt@test.com";
        user.role = UserRole.ADMIN;
        user.tenant = new TenantEntity();
        user.tenant.id = java.util.UUID.randomUUID();
        return jwtGenerator.generateAccessToken(user, Set.of("ADMIN"), 900L);
    }
}
