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
public class CycleDto {

    private UUID id;
    private String code;
    private String libelle;
    private Boolean actif;
}
