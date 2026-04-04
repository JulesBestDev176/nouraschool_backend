package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulletinDto {

    private UUID id;
    private UUID eleveId;
    private String trimestre;
    private String anneeScolaire;
    private Double moyenne;
    private Double moyenneClasse;
    private Integer rang;
    private Integer totalEleves;
    private String appreciation;
    private Integer nombreAbsences;
    private Integer nombreRetards;
    private LocalDateTime createdAt;
    private String fichierPdfUrl;
}
