package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatiereClasseDto {

    private UUID id;
    private UUID matiereId;
    private UUID classeId;
    private UUID enseignantId;
    private String anneeScolaire;
    private Integer volumeHoraire;
}
