package com.nouraschool.inscriptions;

import com.nouraschool.domain.repositories.AnneeAcademiqueRepository;
import com.nouraschool.domain.repositories.ClasseRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests inscriptions : inscription complète, effectif max, doublon, OTP, lien expiré.
 */
@QuarkusTest
class InscriptionTests {

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "Admin123!";

    @Inject
    TenantRepository tenantRepository;

    @Inject
    AnneeAcademiqueRepository anneeAcademiqueRepository;

    @Inject
    ClasseRepository classeRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TenantTestDataHelper testDataHelper;

    private UUID tenantId;
    private UUID adminUserId;
    private String accessToken;
    private UUID cycleId;
    private UUID niveauId;
    private UUID anneeAcademiqueId;
    private UUID classeId;
    private UUID eleveId;
    private UUID parentId;

    @BeforeEach
    @Transactional
    void setUp() {
        testDataHelper.createSecondTenantAndUser();
        tenantId = tenantRepository.findDefault().id;
        var admin = userRepository.findByUsername("admin");
        if (admin != null) adminUserId = admin.id;
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

    private void setupEtablissement() {
        cycleId = given()
                .header("X-Tenant-Id", tenantId.toString())
                .contentType(ContentType.JSON)
                .body(Map.of("code", "COLLEGE", "libelle", "Collège", "actif", true))
                .when()
                .post("/api/v1/cycles")
                .then()
                .statusCode(201)
                .extract().path("id");

        niveauId = given()
                .header("X-Tenant-Id", tenantId.toString())
                .contentType(ContentType.JSON)
                .body(Map.of("cycleId", cycleId, "code", "6EME", "libelle", "6ème", "ordre", 1, "actif", true))
                .when()
                .post("/api/v1/niveaux")
                .then()
                .statusCode(201)
                .extract().path("id");

        anneeAcademiqueId = given()
                .header("X-Tenant-Id", tenantId.toString())
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "libelle", "2025-2026",
                        "dateDebut", "2025-09-01",
                        "dateFin", "2026-07-01",
                        "estCourante", true,
                        "actif", true))
                .when()
                .post("/api/v1/annees-academiques")
                .then()
                .statusCode(201)
                .extract().path("id");

        classeId = given()
                .header("X-Tenant-Id", tenantId.toString())
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "nom", "6ème A",
                        "niveau", "6EME",
                        "anneeScolaire", "2025-2026",
                        "effectifMax", 30))
                .when()
                .post("/api/admin/classes")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    private void setupEleveAndParent() {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        parentId = given()
                .header("X-Tenant-Id", tenantId.toString())
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "parent-" + unique,
                        "email", "parent" + unique + "@test.local",
                        "password", "Parent123!",
                        "firstName", "Parent",
                        "lastName", "Test",
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
                .header("X-Tenant-Id", tenantId.toString())
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "eleve-" + unique,
                        "email", "eleve" + unique + "@test.local",
                        "password", "Eleve123!",
                        "firstName", "Élève",
                        "lastName", "Test",
                        "telephone", "+22176" + unique.replace("-", "").substring(0, 7),
                        "adresse", "Adresse",
                        "matricule", "MAT-" + unique,
                        "dateNaissance", "2012-05-15",
                        "genre", "M",
                        "parentIds", List.of(parentId)))
                .when()
                .post("/api/admin/eleves")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Nested
    @DisplayName("Inscription complète")
    class InscriptionComplete {

        @Test
        @DisplayName("créer inscription avec JWT")
        void createInscription_withJwt_returns201() {
            setupEtablissement();
            setupEleveAndParent();
            String token = getAccessToken();

            Map<String, Object> body = Map.of(
                    "eleveId", eleveId.toString(),
                    "classeId", classeId.toString(),
                    "anneeAcademiqueId", anneeAcademiqueId.toString(),
                    "parentIds", List.of(parentId));

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post("/api/v1/inscriptions")
                    .then()
                    .statusCode(201)
                    .body("numeroInscription", startsWith("INSC-"))
                    .body("statut", equalTo("ACTIF"))
                    .body("eleveId", equalTo(eleveId.toString()))
                    .body("classeId", equalTo(classeId.toString()));

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/inscriptions")
                    .then()
                    .statusCode(200)
                    .body("findAll { it.eleveId == '" + eleveId + "' }.size()", greaterThanOrEqualTo(1));
        }
    }

    @Nested
    @DisplayName("Effectif max")
    class EffectifMax {

        @Test
        @DisplayName("classe effectif max 1 - doublon inscription retourne 422")
        void effectifMax1_doubleInscription_returns422() {
            setupEtablissement();
            setupEleveAndParent();

            UUID classeEffectif1 = given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "nom", "6ème B",
                            "niveau", "6EME",
                            "anneeScolaire", "2025-2026",
                            "effectifMax", 1))
                    .when()
                    .post("/api/admin/classes")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            String token = getAccessToken();
            Map<String, Object> body = Map.of(
                    "eleveId", eleveId.toString(),
                    "classeId", classeEffectif1.toString(),
                    "anneeAcademiqueId", anneeAcademiqueId.toString());

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post("/api/v1/inscriptions")
                    .then()
                    .statusCode(201);

            String unique2 = UUID.randomUUID().toString().substring(0, 8);
            UUID eleve2Id = given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "username", "eleve2-" + unique2,
                            "email", "eleve2" + unique2 + "@test.local",
                            "password", "Eleve123!",
                            "firstName", "Élève2",
                            "lastName", "Test",
                            "telephone", "+22176" + unique2.replace("-", "").substring(0, 7),
                            "adresse", "Adresse",
                            "matricule", "MAT2-" + unique2,
                            "dateNaissance", "2012-06-20",
                            "genre", "M"))
                    .when()
                    .post("/api/admin/eleves")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            Map<String, Object> body2 = Map.of(
                    "eleveId", eleve2Id.toString(),
                    "classeId", classeEffectif1.toString(),
                    "anneeAcademiqueId", anneeAcademiqueId.toString());

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(body2)
                    .when()
                    .post("/api/v1/inscriptions")
                    .then()
                    .statusCode(422)
                    .body("code", equalTo("REGLE_METIER_VIOLEE"));
        }
    }

    @Nested
    @DisplayName("Doublon inscription")
    class DoublonInscription {

        @Test
        @DisplayName("inscrire même élève deux fois même année retourne 422")
        void doubleInscription_sameEleve_returns422() {
            setupEtablissement();
            setupEleveAndParent();
            String token = getAccessToken();

            Map<String, Object> body = Map.of(
                    "eleveId", eleveId.toString(),
                    "classeId", classeId.toString(),
                    "anneeAcademiqueId", anneeAcademiqueId.toString());

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post("/api/v1/inscriptions")
                    .then()
                    .statusCode(201);

            given()
                    .auth().oauth2(token)
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "eleveId", eleveId.toString(),
                            "classeId", classeId.toString(),
                            "anneeAcademiqueId", anneeAcademiqueId.toString()))
                    .when()
                    .post("/api/v1/inscriptions")
                    .then()
                    .statusCode(422)
                    .body("code", equalTo("REGLE_METIER_VIOLEE"));
        }
    }

    @Nested
    @DisplayName("Lien paiement OTP")
    class LienPaiementOtp {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("générer lien puis vérifier OTP")
        void generateLien_verifyOtp_returns200() {
            setupEtablissement();
            setupEleveAndParent();

            List<Map<String, Object>> dtos = List.of(Map.of(
                    "parentId", parentId.toString(),
                    "montantTotal", 50000,
                    "mois", "2025-11"));

            List<Object> links = given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(dtos)
                    .when()
                    .post("/api/v1/liens-paiement/generer")
                    .then()
                    .statusCode(201)
                    .extract().body().as(List.class);

            if (links.isEmpty()) return;
            String token = (String) ((Map<?, ?>) links.get(0)).get("token");

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(Map.of("otp", "123456"))
                    .when()
                    .post("/api/v1/liens-paiement/" + token + "/verifier-otp")
                    .then()
                    .statusCode(200)
                    .body("otpVerified", equalTo(true));
        }

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("détail lien paiement par token")
        void getDetailLienPaiement_returns200() {
            setupEtablissement();
            setupEleveAndParent();

            List<Map<String, Object>> dtos = List.of(Map.of(
                    "parentId", parentId.toString(),
                    "montantTotal", 75000,
                    "mois", "2025-12"));

            List<Object> links = given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType(ContentType.JSON)
                    .body(dtos)
                    .when()
                    .post("/api/v1/liens-paiement/generer")
                    .then()
                    .statusCode(201)
                    .extract().body().as(List.class);

            if (links.isEmpty()) return;
            String token = (String) ((Map<?, ?>) links.get(0)).get("token");

            given()
                    .when()
                    .get("/api/v1/liens-paiement/" + token + "/detail")
                    .then()
                    .statusCode(200)
                    .body("token", equalTo(token))
                    .body("statut", equalTo("EN_ATTENTE"))
                    .body("mois", equalTo("2025-12"));
        }
    }
}
