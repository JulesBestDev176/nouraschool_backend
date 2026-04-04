package com.nouraschool.domain.dtos.platform;

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
public class PlateformeUtilisateurDto {
    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private String rolePlateforme;
    private Boolean actif;
    private Instant createdAt;
}
