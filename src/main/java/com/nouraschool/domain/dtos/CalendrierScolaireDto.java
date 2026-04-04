package com.nouraschool.domain.dtos;

import com.nouraschool.domain.enums.TypeEvenement;
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
public class CalendrierScolaireDto {

    private UUID id;
    private String anneeScolaire;
    private String titre;
    private String description;
    private TypeEvenement typeEvenement;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String concerneClasses;
    private Boolean publier;
}
