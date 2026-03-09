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
public class AppelLigneDto {

    private UUID id;
    private UUID eleveId;
    private String statut; // PRESENT | ABSENT | RETARD
}
