package com.nouraschool.domain.dtos.inscription;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionCreateDto {

    @NotNull(message = "Élève requis")
    private UUID eleveId;

    @NotNull(message = "Classe requise")
    private UUID classeId;

    @NotNull(message = "Année académique requise")
    private UUID anneeAcademiqueId;

    /** IDs des parents à lier à l'élève (optionnel, max 2). */
    private List<UUID> parentIds;
}
