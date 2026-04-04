package com.nouraschool.domain.dtos.etablissement;

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
public class AnneeAcademiqueDto {

    private UUID id;
    private String libelle;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean estCourante;
    private Boolean actif;
}
