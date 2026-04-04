package com.nouraschool.professeur;

import com.nouraschool.domain.dtos.AppelDto;
import com.nouraschool.domain.dtos.AppelLigneDto;
import com.nouraschool.domain.repositories.*;
import com.nouraschool.domain.usecases.enseignant.EnseignantUseCaseImpl;
import com.nouraschool.runtime.tenant.TenantContext;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests Professeur : Appel → absences auto, périmètre professeur (task.md FOLDER 9).
 */
@QuarkusTest
class ProfesseurTests {

    @Inject
    EnseignantUseCaseImpl enseignantUseCase;

    @Inject
    CoursRepository coursRepository;

    @Inject
    EleveRepository eleveRepository;

    @Inject
    AppelRepository appelRepository;

    @Inject
    AbsenceEleveRepository absenceEleveRepository;

    @Inject
    TenantContext tenantContext;

    @Inject
    TenantRepository tenantRepository;

    private UUID tenantId;
    private UUID professeurId;
    private UUID coursId;
    private UUID eleveId;

    @BeforeEach
    void setUp() {
        tenantId = tenantRepository.findDefault() != null ? tenantRepository.findDefault().id : null;
        if (tenantId != null) {
            tenantContext.setTenantId(tenantId);
        }
        var coursList = coursRepository.findByTenantId(tenantId != null ? tenantId : UUID.randomUUID());
        if (!coursList.isEmpty()) {
            coursId = coursList.get(0).id;
            professeurId = coursList.get(0).professeurId;
        }
        var eleves = eleveRepository.findAll();
        if (!eleves.isEmpty()) {
            eleveId = eleves.get(0).id;
        }
    }

    @Test
    @DisplayName("listeAppels avec coursId hors périmètre professeur retourne liste vide")
    void listeAppels_coursNonAssigne_returnsEmpty() {
        UUID fakeProfesseurId = UUID.randomUUID();
        var result = enseignantUseCase.listeAppels(fakeProfesseurId, coursId);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("soumettreAppel avec ligne ABSENT crée une absence")
    void soumettreAppel_ligneAbsent_createsAbsence() {
        if (coursId == null || professeurId == null || eleveId == null || tenantId == null) return;

        AppelDto dto = AppelDto.builder()
                .coursId(coursId)
                .dateCours(LocalDate.now())
                .heureDebut(LocalTime.of(8, 0))
                .statut("BROUILLON")
                .lignes(List.of(
                        AppelLigneDto.builder().eleveId(eleveId).statut("ABSENT").build()
                ))
                .build();

        AppelDto appel = enseignantUseCase.creerAppel(professeurId, dto);
        assertThat(appel.getId()).isNotNull();
        assertThat(appel.getStatut()).isEqualTo("BROUILLON");

        AppelDto soumis = enseignantUseCase.soumettreAppel(professeurId, appel.getId());
        assertThat(soumis.getStatut()).isEqualTo("EN_ATTENTE");

        var absences = absenceEleveRepository.findByEleveId(eleveId);
        assertThat(absences).anyMatch(a -> a.date.equals(LocalDate.now()) && "COURS_SPECIFIQUE".equals(a.typeAbsence.name()));
    }
}
