package com.nouraschool.domain.dtos.viescolaire;

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
public class ConvocationDto {

    private UUID id;
    private UUID parentId;
    private UUID eleveId;
    private String motif;
    private Instant dateConvocation;
    private String statut;
    private String compteRendu;
    private UUID creePar;
}
