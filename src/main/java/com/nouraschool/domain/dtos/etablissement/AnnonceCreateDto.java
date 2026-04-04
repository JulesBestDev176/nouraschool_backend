package com.nouraschool.domain.dtos.etablissement;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceCreateDto {

    @NotBlank(message = "Titre requis")
    private String titre;

    private String contenu;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean actif;
}
