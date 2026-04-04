package com.nouraschool.auth;

import com.nouraschool.domain.services.RedisService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration pour AuthResource (POST /login, /refresh, GET /me, POST /logout, forgot-password, reset-password, change-password).
 * Utilise l'utilisateur admin seedé (admin / Admin123!).
 */
@QuarkusTest
class AuthResourceIT {

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "Admin123!";
    private static final String ADMIN_EMAIL = "admin@noura-school.com";

    @Inject
    RedisService redisService;

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class Login {

        @Test
        @DisplayName("identifiants valides retournent 200 avec accessToken et refreshToken")
        void validCredentials_returnsTokens() {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .body("accessToken", not(blankOrNullString()))
                    .body("refreshToken", not(blankOrNullString()))
                    .body("tokenType", equalTo("Bearer"))
                    .body("expiresIn", notNullValue());
        }

        @Test
        @DisplayName("login par email fonctionne")
        void loginByEmail_returnsTokens() {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_EMAIL, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .body("accessToken", not(blankOrNullString()));
        }

        @Test
        @DisplayName("identifiants invalides retournent 401")
        void invalidCredentials_returns401() {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", "WrongPassword"))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(401)
                    .body("code", equalTo("IDENTIFIANTS_INVALIDES"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/refresh")
    class Refresh {

        @Test
        @DisplayName("refresh token valide retourne nouvel accessToken")
        void validRefreshToken_returnsNewAccessToken() {
            String refreshToken = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .extract().path("refreshToken");

            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("refreshToken", refreshToken))
                    .when()
                    .post("/api/v1/auth/refresh")
                    .then()
                    .statusCode(200)
                    .body("accessToken", not(blankOrNullString()))
                    .body("refreshToken", not(blankOrNullString()));
        }

        @Test
        @DisplayName("refresh token invalide retourne 401")
        void invalidRefreshToken_returns401() {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("refreshToken", "invalid-token"))
                    .when()
                    .post("/api/v1/auth/refresh")
                    .then()
                    .statusCode(401);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/me")
    class Me {

        @Test
        @DisplayName("sans token retourne 401")
        void withoutToken_returns401() {
            given()
                    .when()
                    .get("/api/v1/auth/me")
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("avec Bearer valide retourne 200 et infos utilisateur")
        void withValidToken_returnsUserInfo() {
            String accessToken = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .extract().path("accessToken");

            given()
                    .auth().oauth2(accessToken)
                    .when()
                    .get("/api/v1/auth/me")
                    .then()
                    .statusCode(200)
                    .body("username", equalTo(ADMIN_LOGIN))
                    .body("email", equalTo(ADMIN_EMAIL))
                    .body("role", equalTo("ADMIN"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout")
    class Logout {

        @Test
        @DisplayName("logout avec token valide retourne 204")
        void withValidToken_returns204() {
            String accessToken = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .extract().path("accessToken");

            given()
                    .auth().oauth2(accessToken)
                    .when()
                    .post("/api/v1/auth/logout")
                    .then()
                    .statusCode(204);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/forgot-password")
    class ForgotPassword {

        @Test
        @DisplayName("retourne 204 même pour email inconnu (pas de fuite)")
        void anyEmail_returns204() {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", "unknown@example.com"))
                    .when()
                    .post("/api/v1/auth/forgot-password")
                    .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("email valide retourne 204")
        void validEmail_returns204() {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", ADMIN_EMAIL))
                    .when()
                    .post("/api/v1/auth/forgot-password")
                    .then()
                    .statusCode(204);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/reset-password")
    class ResetPassword {

        @Test
        @DisplayName("token invalide retourne 400")
        void invalidToken_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("token", "invalid-token", "newPassword", "NewPass123!x"))
                    .when()
                    .post("/api/v1/auth/reset-password")
                    .then()
                    .statusCode(400)
                    .body("code", equalTo("RESET_TOKEN_INVALIDE"));
        }

        @Test
        @DisplayName("token valide (injecté Redis) met à jour le mot de passe")
        void validToken_updatesPassword() {
            String accessToken = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .extract().path("accessToken");
            String userId = given().auth().oauth2(accessToken).when().get("/api/v1/auth/me").then().extract().path("id").toString();
            String resetToken = "test-reset-" + UUID.randomUUID();
            redisService.set("auth:reset:" + resetToken, userId, 3600);

            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("token", resetToken, "newPassword", "NewPass123!x"))
                    .when()
                    .post("/api/v1/auth/reset-password")
                    .then()
                    .statusCode(204);

            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", "NewPass123!x"))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200);

            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(401);

            String restoreToken = "restore-" + UUID.randomUUID();
            redisService.set("auth:reset:" + restoreToken, userId, 3600);
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("token", restoreToken, "newPassword", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/reset-password")
                    .then()
                    .statusCode(204);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/change-password")
    class ChangePassword {

        @Test
        @DisplayName("sans token retourne 401")
        void withoutToken_returns401() {
            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("oldPassword", ADMIN_PASSWORD, "newPassword", "NewPass123!x"))
                    .when()
                    .post("/api/v1/auth/change-password")
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("ancien mot de passe incorrect retourne 401")
        void wrongOldPassword_returns401() {
            String accessToken = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .extract().path("accessToken");

            given()
                    .auth().oauth2(accessToken)
                    .contentType(ContentType.JSON)
                    .body(Map.of("oldPassword", "WrongOld", "newPassword", "NewPass123!x"))
                    .when()
                    .post("/api/v1/auth/change-password")
                    .then()
                    .statusCode(401)
                    .body("code", equalTo("MOT_DE_PASSE_ACTUEL_INCORRECT"));
        }

        @Test
        @DisplayName("ancien correct met à jour et retourne 204")
        void validChange_returns204() {
            String accessToken = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .extract().path("accessToken");

            given()
                    .auth().oauth2(accessToken)
                    .contentType(ContentType.JSON)
                    .body(Map.of("oldPassword", ADMIN_PASSWORD, "newPassword", "ChangedPass123!"))
                    .when()
                    .post("/api/v1/auth/change-password")
                    .then()
                    .statusCode(204);

            given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", "ChangedPass123!"))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200);

            // Restore for other tests
            String newToken = given().contentType(ContentType.JSON).body(Map.of("login", ADMIN_LOGIN, "password", "ChangedPass123!")).when().post("/api/v1/auth/login").then().extract().path("accessToken");
            given().auth().oauth2(newToken).contentType(ContentType.JSON).body(Map.of("oldPassword", "ChangedPass123!", "newPassword", ADMIN_PASSWORD)).when().post("/api/v1/auth/change-password").then().statusCode(204);
        }
    }
}
