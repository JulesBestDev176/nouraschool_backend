package com.nouraschool.platform;

import com.nouraschool.domain.dtos.platform.TenantCreateDto;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.TenantService;
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
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests plateforme : création tenant (admin initial), suspension, accès GESTIONNAIRE vs SUPER_ADMIN.
 */
@QuarkusTest
class PlateformeTests {

    private static final String BASE_PATH = "/api/platform/tenants";

    @Inject
    TenantService tenantService;

    @Inject
    TenantRepository tenantRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TenantTestDataHelper testDataHelper;

    @BeforeEach
    @Transactional
    void setUp() {
        testDataHelper.createSecondTenantAndUser();
    }

    @Nested
    @DisplayName("Création tenant → admin initial créé")
    class CreateTenantAdmin {

        @Test
        @Transactional
        @DisplayName("création tenant crée l'admin admin-{slug}")
        void createTenant_createsInitialAdmin() {
            String slug = "test-plateforme-" + UUID.randomUUID().toString().substring(0, 8);
            TenantCreateDto dto = new TenantCreateDto();
            dto.setSlug(slug);
            dto.setNom("Test Plateforme");
            dto.setPlan("TRIAL");
            dto.setInitialAdminEmail("admin-plateforme@test.local");
            dto.setInitialAdminPassword("Secret123!");

            tenantService.create(dto);

            var admin = userRepository.findByUsername("admin-" + slug);
            assertThat(admin).isNotNull();
            assertThat(admin.email).isEqualTo("admin-plateforme@test.local");
            assertThat(admin.tenant).isNotNull();
            assertThat(admin.tenant.slug).isEqualTo(slug);
        }

        @Test
        @Transactional
        @DisplayName("création tenant sans initialAdmin crée admin avec email par défaut")
        void createTenant_withoutInitialAdmin_createsAdminWithDefaultEmail() {
            String slug = "test-plateforme2-" + UUID.randomUUID().toString().substring(0, 8);
            TenantCreateDto dto = new TenantCreateDto();
            dto.setSlug(slug);
            dto.setNom("Test Plateforme 2");
            dto.setPlan("TRIAL");

            tenantService.create(dto);

            var admin = userRepository.findByUsername("admin-" + slug);
            assertThat(admin).isNotNull();
            assertThat(admin.email).isEqualTo("admin@" + slug + ".noura-school.local");
        }
    }

    @Nested
    @DisplayName("Suspension tenant → users bloqués")
    class SuspendTenant {

        @Test
        @TestSecurity(user = "admin", roles = "SUPER_ADMIN")
        @Transactional
        @DisplayName("suspend tenant puis login admin renvoie TENANT_INACTIF")
        void suspendTenant_thenLogin_adminBlocked() {
            String slug = "test-suspend-" + UUID.randomUUID().toString().substring(0, 8);
            TenantCreateDto dto = new TenantCreateDto();
            dto.setSlug(slug);
            dto.setNom("Tenant Suspend Test");
            dto.setPlan("TRIAL");
            dto.setInitialAdminEmail("admin-suspend@test.local");
            dto.setInitialAdminPassword("Secret123!");
            var tenant = tenantService.create(dto);
            var admin = userRepository.findByUsername("admin-" + slug);
            assertThat(admin).isNotNull();

            tenantService.suspend(tenant.getId());

            given()
                    .contentType("application/json")
                    .body(Map.of(
                            "login", "admin-suspend@test.local",
                            "password", "Secret123!"))
                    .when()
                    .post("/api/v1/auth/login")
                    .then()
                    .statusCode(403)
                    .body("code", equalTo("TENANT_INACTIF"));
        }
    }

    @Nested
    @DisplayName("GESTIONNAIRE vs SUPER_ADMIN")
    class GestionsnaireVsSuperAdmin {

        @Test
        @TestSecurity(user = "gestionnaire", roles = "GESTIONNAIRE")
        @DisplayName("GESTIONNAIRE ne peut pas suspendre un tenant")
        void gestionnaire_suspend_returns403() {
            UUID tenantId = tenantRepository.findBySlug("tenant-test-iso")
                    .map(t -> t.id)
                    .orElseGet(() -> tenantRepository.findAll().stream()
                            .filter(t -> !"default".equals(t.slug))
                            .findFirst()
                            .map(t -> t.id)
                            .orElseThrow());

            given()
                    .when()
                    .post(BASE_PATH + "/" + tenantId + "/suspend")
                    .then()
                    .statusCode(403);
        }

        @Test
        @TestSecurity(user = "gestionnaire", roles = "GESTIONNAIRE")
        @DisplayName("GESTIONNAIRE ne peut pas supprimer un tenant")
        void gestionnaire_delete_returns403() {
            UUID tenantId = tenantRepository.findBySlug("tenant-test-iso")
                    .map(t -> t.id)
                    .orElseGet(() -> tenantRepository.findAll().stream()
                            .filter(t -> !"default".equals(t.slug))
                            .findFirst()
                            .map(t -> t.id)
                            .orElseThrow());

            given()
                    .when()
                    .delete(BASE_PATH + "/" + tenantId)
                    .then()
                    .statusCode(403);
        }

        @Test
        @TestSecurity(user = "superadmin", roles = "SUPER_ADMIN")
        @DisplayName("SUPER_ADMIN peut suspendre un tenant (hors default)")
        void superAdmin_suspend_returns204() {
            Optional<TenantEntity> tenant = tenantRepository.findBySlug("tenant-test-iso");
            if (tenant.isEmpty()) {
                return;
            }

            given()
                    .when()
                    .post(BASE_PATH + "/" + tenant.get().id + "/suspend")
                    .then()
                    .statusCode(204);
        }
    }
}
