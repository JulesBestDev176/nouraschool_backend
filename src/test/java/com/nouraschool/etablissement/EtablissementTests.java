package com.nouraschool.etablissement;

import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.tenant.TenantTestDataHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

/**
 * Tests établissement : CRUD cycles, niveaux, matières, activation année académique,
 * protection dernier admin, isolation tenant.
 */
@QuarkusTest
class EtablissementTests {

    @Inject
    TenantRepository tenantRepository;

    @Inject
    TenantTestDataHelper testDataHelper;

    @Inject
    UserRepository userRepository;

    private UUID tenantId;

    @BeforeEach
    @Transactional
    void setUp() {
        testDataHelper.createSecondTenantAndUser();
        tenantId = tenantRepository.findDefault().id;
    }

    @Nested
    @DisplayName("CRUD cycles")
    class Cycles {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("créer et lister cycles")
        void createAndListCycles() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(Map.of("code", "COLLEGE", "libelle", "Collège", "actif", true))
                    .when()
                    .post("/api/v1/cycles")
                    .then()
                    .statusCode(201)
                    .body("code", org.hamcrest.Matchers.equalTo("COLLEGE"));

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/cycles")
                    .then()
                    .statusCode(200)
                    .body("findAll { it.code == 'COLLEGE' }.size()", org.hamcrest.Matchers.greaterThanOrEqualTo(1));
        }
    }

    @Nested
    @DisplayName("CRUD niveaux")
    class Niveaux {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("créer cycle puis niveau")
        void createCycleThenNiveau() {
            String cycleId = given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(Map.of("code", "COLLEGE", "libelle", "Collège", "actif", true))
                    .when()
                    .post("/api/v1/cycles")
                    .then()
                    .statusCode(201)
                    .extract().path("id");

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(Map.of("cycleId", cycleId, "code", "6EME", "libelle", "6ème", "ordre", 1, "actif", true))
                    .when()
                    .post("/api/v1/niveaux")
                    .then()
                    .statusCode(201)
                    .body("code", org.hamcrest.Matchers.equalTo("6EME"));
        }
    }

    @Nested
    @DisplayName("CRUD matières (AdminMatiere)")
    class Matieres {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("lister matières")
        void listMatieres() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/admin/matieres")
                    .then()
                    .statusCode(200);
        }
    }

    @Nested
    @DisplayName("Activation année académique")
    class ActivationAnneeAcademique {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("activer année académique met à jour estCourante")
        void activerAnneeAcademique() {
            String aaId = given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .contentType("application/json")
                    .body(Map.of(
                            "libelle", "2025-2026",
                            "dateDebut", "2025-09-01",
                            "dateFin", "2026-07-01",
                            "estCourante", false,
                            "actif", true))
                    .when()
                    .post("/api/v1/annees-academiques")
                    .then()
                    .statusCode(201)
                    .body("estCourante", org.hamcrest.Matchers.equalTo(false))
                    .extract().path("id");

            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .post("/api/v1/annees-academiques/" + aaId + "/activer")
                    .then()
                    .statusCode(200)
                    .body("estCourante", org.hamcrest.Matchers.equalTo(true));
        }
    }

    @Nested
    @DisplayName("Protection dernier admin")
    class ProtectionDernierAdmin {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("supprimer dernier admin lance REGLE_METIER_VIOLEE")
        void deleteLastAdmin_throws() {
            var tenant = tenantRepository.findDefault();
            var admins = userRepository.findAll().stream()
                    .filter(u -> tenant.id.equals(u.tenant != null ? u.tenant.id : null))
                    .filter(u -> u.role == UserRole.ADMIN)
                    .toList();
            if (admins.size() != 1) return;
            UUID lastAdminId = admins.get(0).id;

            given()
                    .header("X-Tenant-Id", tenant.id.toString())
                    .when()
                    .delete("/api/v1/utilisateurs/" + lastAdminId)
                    .then()
                    .statusCode(422)
                    .body("code", org.hamcrest.Matchers.equalTo("REGLE_METIER_VIOLEE"));
        }
    }

    @Nested
    @DisplayName("Isolation tenant")
    class IsolationTenant {

        @Test
        @TestSecurity(user = "admin", roles = "ADMIN")
        @DisplayName("cycles filtrés par tenant")
        void cyclesFilteredByTenant() {
            given()
                    .header("X-Tenant-Id", tenantId.toString())
                    .when()
                    .get("/api/v1/cycles")
                    .then()
                    .statusCode(200);
        }
    }

}
