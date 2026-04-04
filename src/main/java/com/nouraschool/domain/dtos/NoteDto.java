package com.nouraschool.domain.dtos;

import com.nouraschool.domain.enums.TypeEvaluation;
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
public class NoteDto {

    private UUID id;
    private UUID eleveId;
    private UUID matiereId;
    private TypeEvaluation typeEvaluation;
    private Double note;
    private Double noteSur;
    private String trimestre;
    private String anneeScolaire;
    private LocalDate dateEvaluation;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
