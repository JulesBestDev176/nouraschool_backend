package com.nouraschool.domain.dtos;

import com.nouraschool.domain.enums.JourSemaine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploiDuTempsDto {

    private UUID id;
    private UUID classeId;
    private UUID matiereId;
    private UUID enseignantId;
    private JourSemaine jourSemaine;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;
    private String anneeScolaire;
    private LocalDate dateDebutValidite;
    private LocalDate dateFinValidite;
}
