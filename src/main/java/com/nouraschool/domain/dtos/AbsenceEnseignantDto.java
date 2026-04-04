package com.nouraschool.domain.dtos;

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
public class AbsenceEnseignantDto {

    private UUID id;
    private UUID enseignantId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private Boolean justifiee;
    private String documentJustificatifUrl;
    private UUID remplacantId;
    private Boolean notificationEnvoyee;
    private LocalDateTime createdAt;
}
