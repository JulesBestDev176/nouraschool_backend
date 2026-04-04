package com.nouraschool.domain.dtos.rh;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointageCreateDto {

    @NotNull(message = "Utilisateur requis")
    private UUID utilisateurId;

    @NotBlank(message = "Type de pointage requis")
    private String typePointage;

    @NotNull(message = "Date/heure requise")
    private Instant dateHeure;

    private String methode;
    private UUID createdBy;
}
