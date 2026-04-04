package com.nouraschool.domain.dtos.etablissement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtablissementStatsDto {

    private long nbClasses;
    private long nbEleves;
    private long nbEnseignants;
    private long nbMatieres;
}
