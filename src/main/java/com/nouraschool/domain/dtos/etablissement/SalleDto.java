package com.nouraschool.domain.dtos.etablissement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalleDto {

    private UUID id;
    private UUID batimentId;
    private String nom;
    private Integer capacite;
    private String typeSalle;
    private Boolean actif;
}
