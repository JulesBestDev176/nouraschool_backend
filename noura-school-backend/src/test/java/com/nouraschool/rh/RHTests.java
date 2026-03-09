package com.nouraschool.rh;

import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.tenant.TenantTestDataHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests RH : pointage, validation absence, permissions RH vs ADMIN.
 */
@QuarkusTest
class RHTests {

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "Admin123!";

    @Inject
    TenantRepository tenantRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TenantTestDataHelper testDataHelper;

    private UUID tenantId;
    private UUID adminUserId;
    private String accessToken;

    @BeforeEach
    @Transactional
    void setUp() {
        testDataHelper.createSecondTenantAndUser();
        tenantId = tenantRepository.findDefault().id;
        var admin = userRepository.findByUsername("admin");
        if (admin != null) {
            adminUserId = admin.id;
        }
    }

    private String getAccessToken() {
        if (accessToken == null) {
            accessToken = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("login", ADMIN_LOGIN, "password", ADMIN_PASSWORD))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(200)
                    .extract().path("accessToken");
        }
        return accessToken;
    }

    @Nested
    @DisplayName("Pointage")
    class Pointage {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("créer pointage et lister")
        void createAndListPointage() {
            if (adminUserId == null) return;
            Map<String, Object> body = Map.of(
                    "utilisateurId", adminUserId.toString(),
                    "typePointage", "ENTREE",
                    "dateHeure", Instant.now().toString());

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(body)
                    .when()
                    .post("/api/v1/pointages")
                    .then()
                    .statusCode(201)
                    .body("typePointage", equalTo("ENTREE"))
                    .body("utilisateurId", equalTo(adminUserId.toString()));

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/pointages")
                    .then()
                    .statusCode(200)
                    .body("findAll { it.typePointage == 'ENTREE' }.size()", greaterThanOrEqualTo(1));
        }

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("rapport pointage sur une période")
        void getRapportPointage() {
            LocalDate debut = LocalDate.now();
            LocalDate fin = LocalDate.now();

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .queryParam("debut", debut.toString())
                    .queryParam("fin", fin.toString())
                    .when()
                    .get("/api/v1/pointages/rapport")
                    .then()
                    .statusCode(200)
                    .body("dateDebut", notNullValue())
                    .body("dateFin", notNullValue())
                    .body("pointages", notNullValue());
        }
    }

    @Nested
    @DisplayName("Absence personnel")
    class AbsencePersonnel {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("créer et lister absences")
        void createAndListAbsences() {
            if (adminUserId == null) return;
            Map<String, Object> body = Map.of(
                    "utilisateurId", adminUserId.toString(),
                    "dateDebut", LocalDate.now().toString(),
                    "dateFin", LocalDate.now().plusDays(2).toString(),
                    "motif", "Congés",
                    "typeAbsence", "CONGES");

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(body)
                    .when()
                    .post("/api/v1/absences-personnel")
                    .then()
                    .statusCode(201)
                    .body("statut", equalTo("EN_ATTENTE"))
                    .body("motif", equalTo("Congés"));

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/absences-personnel")
                    .then()
                    .statusCode(200)
                    .body("findAll { it.motif == 'Congés' }.size()", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("valider absence avec JWT retourne 200")
        void validerAbsence_withJwt_returns200() {
            if (adminUserId == null) return;
            String token = getAccessToken();

            Map<String, Object> body = Map.of(
                    "utilisateurId", adminUserId.toString(),
                    "dateDebut", LocalDate.now().toString(),
                    "dateFin", LocalDate.now().plusDays(1).toString(),
                    "motif", "RTT",
                    "typeAbsence", "RTT");

            String absenceId = given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(body)
                    .when()
                    .post("/api/v1/absences-personnel")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .patch("/api/v1/absences-personnel/" + absenceId + "/valider")
                    .then()
                    .statusCode(200)
                    .body("statut", equalTo("VALIDE"));
        }

        @Test
        @DisplayName("refuser absence avec JWT retourne 200")
        void refuserAbsence_withJwt_returns200() {
            if (adminUserId == null) return;
            String token = getAccessToken();

            Map<String, Object> body = Map.of(
                    "utilisateurId", adminUserId.toString(),
                    "dateDebut", LocalDate.now().plusDays(10).toString(),
                    "dateFin", LocalDate.now().plusDays(12).toString(),
                    "motif", "Test refus",
                    "typeAbsence", "CONGES");

            String absenceId = given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(body)
                    .when()
                    .post("/api/v1/absences-personnel")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .patch("/api/v1/absences-personnel/" + absenceId + "/refuser")
                    .then()
                    .statusCode(200)
                    .body("statut", equalTo("REFUSE"));
        }

        @Test
        @DisplayName("valider une absence déjà validée retourne 422")
        void validerAbsence_dejaValidee_returns422() {
            if (adminUserId == null) return;
            String token = getAccessToken();

            Map<String, Object> body = Map.of(
                    "utilisateurId", adminUserId.toString(),
                    "dateDebut", LocalDate.now().plusDays(20).toString(),
                    "dateFin", LocalDate.now().plusDays(22).toString(),
                    "motif", "Double validation test",
                    "typeAbsence", "CONGES");

            String absenceId = given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(body)
                    .when()
                    .post("/api/v1/absences-personnel")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .patch("/api/v1/absences-personnel/" + absenceId + "/valider")
                    .then()
                    .statusCode(200);

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .patch("/api/v1/absences-personnel/" + absenceId + "/valider")
                    .then()
                    .statusCode(422)
                    .body("code", equalTo("REGLE_METIER_VIOLEE"));
        }
    }

    @Nested
    @DisplayName("Permissions RH vs ADMIN")
    class Permissions {

        @Test
        @TestSecurity(user = "rh", roles = "RH")
        @DisplayName("RH peut créer un pointage")
        void rh_canCreatePointage() {
            if (adminUserId == null) return;

            Map<String, Object> body = Map.of(
                    "utilisateurId", adminUserId.toString(),
                    "typePointage", "SORTIE",
                    "dateHeure", Instant.now().toString());

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(body)
                    .when()
                    .post("/api/v1/pointages")
                    .then()
                    .statusCode(201);
        }

        @Test
        @TestSecurity(user = "rh", roles = "RH")
        @DisplayName("RH peut lister les absences")
        void rh_canListAbsences() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/absences-personnel")
                    .then()
                    .statusCode(200);
        }

        @Test
        @TestSecurity(user = "enseignant", roles = "ENSEIGNANT")
        @DisplayName("ENSEIGNANT ne peut pas accéder aux pointages")
        void enseignant_cannotAccessPointages() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/pointages")
                    .then()
                    .statusCode(403);
        }

        @Test
        @TestSecurity(user = "enseignant", roles = "ENSEIGNANT")
        @DisplayName("ENSEIGNANT ne peut pas accéder aux absences-personnel")
        void enseignant_cannotAccessAbsencesPersonnel() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/absences-personnel")
                    .then()
                    .statusCode(403);
        }

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("ADMIN peut accéder au personnel")
        void admin_canAccessPersonnel() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/personnel")
                    .then()
                    .statusCode(200);
        }
    }
}
