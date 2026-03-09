package com.nouraschool.tenant;

import com.nouraschool.domain.repositories.TenantRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Vérifie l'isolation cross-tenant : les utilisateurs d'un tenant ne sont pas visibles avec un autre X-Tenant-Id.
 */
@QuarkusTest
public class TenantIsolationTest {

    @Inject
    TenantRepository tenantRepository;

    @Inject
    TenantTestDataHelper testDataHelper;

    private UUID defaultTenantId;
    private UUID tenant2Id;

    @BeforeEach
    @Transactional
    public void setUp() {
        testDataHelper.createSecondTenantAndUser();
        defaultTenantId = tenantRepository.findDefault().id;
        tenant2Id = tenantRepository.findBySlug("tenant-test-iso").orElseThrow().id;
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    public void listUsers_withDefaultTenant_returnsOnlyDefaultTenantUsers() {
        given()
                .header("X-Tenant-Id", defaultTenantId.toString())
                .when()
                .get("/api/admin/users")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("findAll { it.username == 'user-tenant2' }.size()", is(0));
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    public void listUsers_withTenant2_returnsOnlyTenant2Users() {
        given()
                .header("X-Tenant-Id", tenant2Id.toString())
                .when()
                .get("/api/admin/users")
                .then()
                .statusCode(200)
                .body("findAll { it.username == 'user-tenant2' }.size()", greaterThanOrEqualTo(1))
                .body("findAll { it.username == 'admin' }.size()", is(0));
    }

    @Test
    public void request_withInvalidTenantId_returns403() {
        given()
                .header("X-Tenant-Id", "00000000-0000-0000-0000-000000000000")
                .when()
                .get("/api/admin/users")
                .then()
                .statusCode(403);
    }
}
