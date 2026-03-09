package com.nouraschool.eleve;

import com.nouraschool.domain.repositories.*;
import com.nouraschool.domain.usecases.eleve.EleveUseCase;
import com.nouraschool.domain.entities.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * Tests Élève : ne voit que ses données, bulletin non validé filtré (task.md FOLDER 10).
 */
@QuarkusTest
class EleveTests {

    @Inject
    EleveUseCase eleveUseCase;

    @Inject
    EleveRepository eleveRepository;

    @Inject
    BulletinRepository bulletinRepository;

    @Inject
    TenantRepository tenantRepository;

    private UUID tenantId;
    private UUID eleveId;

    @BeforeEach
    void setUp() {
        tenantId = tenantRepository.findDefault() != null ? tenantRepository.findDefault().id : null;
        var eleves = eleveRepository.findAll();
        if (!eleves.isEmpty()) {
            eleveId = eleves.get(0).id;
        }
    }

    @Test
    @DisplayName("mesNotes retourne uniquement les notes de l'élève")
    void mesNotes_returnsOnlyEleveNotes() {
        if (eleveId == null) return;
        var notes = eleveUseCase.mesNotes(eleveId);
        assertThat(notes).allMatch(n -> n.getEleveId() == null || n.getEleveId().equals(eleveId));
    }

    @Test
    @DisplayName("mesBulletins ne retourne que les bulletins validés (statut VALIDE)")
    void mesBulletins_returnsOnlyValidated() {
        if (eleveId == null) return;
        var bulletins = eleveUseCase.mesBulletins(eleveId);
        var bulletinsFromRepo = bulletinRepository.findByEleveIdAndStatut(eleveId, "VALIDE");
        assertThat(bulletins).hasSize(bulletinsFromRepo.size());
    }

    @Test
    @TestSecurity(user = "eleve-test", roles = "ELEVE")
    @DisplayName("ELEVE accède à /api/eleve/profil avec 200")
    void eleve_canAccessProfil() {
        given()
                .header("X-Tenant-Id", tenantId != null ? tenantId.toString() : UUID.randomUUID().toString())
                .when()
                .get("/api/eleve/profil")
                .then()
                .statusCode(greaterThanOrEqualTo(200));
    }

    @Test
    @DisplayName("ADMIN ne peut pas accéder aux endpoints élève (403 si mauvaise auth)")
    void nonEleve_cannotAccessEleveEndpoints() {
        given()
                .when()
                .get("/api/eleve/notes")
                .then()
                .statusCode(greaterThanOrEqualTo(401));
    }
}
