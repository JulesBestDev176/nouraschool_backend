package com.nouraschool.domain.dtos.rh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbsencePersonnelDto {

    private UUID id;
    private UUID utilisateurId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private String typeAbsence;
    private String justificatifUrl;
    private String statut;
    private UUID validePar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
