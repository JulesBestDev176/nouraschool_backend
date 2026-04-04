package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClasseDto {

    private UUID id;
    private String nom;
    private String niveau;
    private String anneeScolaire;
    private Integer effectifMax;
    private String salleClasse;
}
