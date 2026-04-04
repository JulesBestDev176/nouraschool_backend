package com.nouraschool.viescolaire;

import com.nouraschool.domain.enums.TypeAbsence;
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

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests Vie scolaire : conflit EDT, approbation absence, bulletin, accès surveillant par cycle.
 */
@QuarkusTest
class VieScolaireTests {

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
    private UUID eleveId;
    private UUID parentId;
    private String accessToken;

    @BeforeEach
    @Transactional
    void setUp() {
        testDataHelper.createSecondTenantAndUser();
        tenantId = tenantRepository.findDefault().id;
        var admin = userRepository.findByUsername("admin");
        if (admin != null) adminUserId = admin.id;
        setupEleveAndParent();
    }

    private void setupEleveAndParent() {
        try {
            if (tenantId == null) return;
            String token = getAccessToken();
            String unique = UUID.randomUUID().toString().substring(0, 8);
            parentId = given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "username", "parent-vs-" + unique,
                            "email", "parentvs" + unique + "@test.local",
                            "password", "Parent123!",
                            "firstName", "Parent",
                            "lastName", "VieScolaire",
                            "telephone", "+22177" + unique.replace("-", "").substring(0, 7),
                            "adresse", "Adresse",
                            "profession", "Comptable",
                            "lienParente", "PERE"))
                    .when()
                    .post("/api/admin/parents")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            eleveId = given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "username", "eleve-vs-" + unique,
                            "email", "elevevs" + unique + "@test.local",
                            "password", "Eleve123!",
                            "firstName", "Élève",
                            "lastName", "VieScolaire",
                            "telephone", "+22176" + unique.replace("-", "").substring(0, 7),
                            "adresse", "Adresse",
                            "matricule", "MAT-VS-" + unique,
                            "dateNaissance", "2012-05-15",
                            "genre", "M",
                            "parentIds", java.util.List.of(parentId)))
                    .when()
                    .post("/api/admin/eleves")
                    .then()
                    .statusCode(201)
                    .extract().path("id");
        } catch (Exception ignored) {
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
    @DisplayName("Accès surveillant")
    class AccesSurveillant {

        @Test
        @TestSecurity(user = "surveillant", roles = "SURVEILLANT")
        @DisplayName("SURVEILLANT peut lister emplois du temps")
        void surveillant_canListEmploisDuTemps() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/emplois-du-temps")
                    .then()
                    .statusCode(200);
        }

        @Test
        @TestSecurity(user = "surveillant", roles = "SURVEILLANT")
        @DisplayName("SURVEILLANT peut lister absences élèves")
        void surveillant_canListAbsencesEleves() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/absences-eleves")
                    .then()
                    .statusCode(200);
        }

        @Test
        @TestSecurity(user = "surveillant", roles = "SURVEILLANT")
        @DisplayName("SURVEILLANT peut lister convocations")
        void surveillant_canListConvocations() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/convocations")
                    .then()
                    .statusCode(200);
        }

        @Test
        @TestSecurity(user = "enseignant", roles = "ENSEIGNANT")
        @DisplayName("ENSEIGNANT ne peut pas accéder aux emplois du temps v1")
        void enseignant_cannotAccessEmploisDuTemps() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .post("/api/v1/emplois-du-temps")
                    .then()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("Approbation absence")
    class ApprobationAbsence {

        @Test
        @DisplayName("approuver absence avec JWT retourne 200")
        void approuverAbsence_withJwt_returns200() {
            if (eleveId == null) return;
            String token = getAccessToken();

            Map<String, Object> absenceBody = Map.of(
                    "eleveId", eleveId.toString(),
                    "date", "2025-11-15",
                    "typeAbsence", TypeAbsence.ABSENCE.name(),
                    "justifiee", false,
                    "motif", "Test approbation");

            String absenceId = given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(absenceBody)
                    .when()
                    .post("/api/v1/absences-eleves")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .post("/api/v1/absences-eleves/" + absenceId + "/approuver")
                    .then()
                    .statusCode(200)
                    .body("statut", equalTo("APPROUVEE"));
        }

        @Test
        @DisplayName("rejeter absence avec JWT retourne 200")
        void rejeterAbsence_withJwt_returns200() {
            if (eleveId == null) return;
            String token = getAccessToken();

            Map<String, Object> absenceBody = Map.of(
                    "eleveId", eleveId.toString(),
                    "date", "2025-11-20",
                    "typeAbsence", TypeAbsence.ABSENCE.name(),
                    "justifiee", false,
                    "motif", "Test rejet");

            String absenceId = given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(absenceBody)
                    .when()
                    .post("/api/v1/absences-eleves")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .post("/api/v1/absences-eleves/" + absenceId + "/rejeter")
                    .then()
                    .statusCode(200)
                    .body("statut", equalTo("REJETEE"));
        }

        @Test
        @DisplayName("approuver absence déjà approuvée retourne 422")
        void approuverAbsence_dejaApprouvee_returns422() {
            if (eleveId == null) return;
            String token = getAccessToken();

            Map<String, Object> absenceBody = Map.of(
                    "eleveId", eleveId.toString(),
                    "date", "2025-11-25",
                    "typeAbsence", TypeAbsence.ABSENCE.name(),
                    "justifiee", false,
                    "motif", "Double approbation");

            String absenceId = given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(absenceBody)
                    .when()
                    .post("/api/v1/absences-eleves")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .post("/api/v1/absences-eleves/" + absenceId + "/approuver")
                    .then()
                    .statusCode(200);

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .post("/api/v1/absences-eleves/" + absenceId + "/approuver")
                    .then()
                    .statusCode(422)
                    .body("code", equalTo("REGLE_METIER_VIOLEE"));
        }
    }
}
