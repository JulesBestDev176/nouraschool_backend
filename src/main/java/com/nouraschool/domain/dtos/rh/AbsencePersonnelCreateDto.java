package com.nouraschool.domain.dtos.rh;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbsencePersonnelCreateDto {

    @NotNull(message = "Utilisateur requis")
    private UUID utilisateurId;

    @NotNull(message = "Date de début requise")
    private LocalDate dateDebut;

    @NotNull(message = "Date de fin requise")
    private LocalDate dateFin;

    private String motif;
    private String typeAbsence;
    private String justificatifUrl;
}
