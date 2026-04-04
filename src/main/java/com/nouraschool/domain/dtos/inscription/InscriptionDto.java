package com.nouraschool.domain.dtos.inscription;

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
public class InscriptionDto {

    private UUID id;
    private String numeroInscription;
    private UUID eleveId;
    private UUID classeId;
    private UUID anneeAcademiqueId;
    private String statut;
    private UUID creePar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
