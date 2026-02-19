package com.nouraschool.domain.dtos;

import com.nouraschool.domain.enums.StatutReclamation;
import com.nouraschool.domain.enums.TypeReclamation;
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
public class ReclamationDto {

    private UUID id;
    private UUID eleveId;
    private TypeReclamation typeReclamation;
    private String objet;
    private String description;
    private UUID noteId;
    private UUID absenceId;
    private StatutReclamation statut;
    private String reponse;
    private UUID traitePar;
    private LocalDateTime dateTraitement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
