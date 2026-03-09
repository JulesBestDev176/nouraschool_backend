package com.nouraschool.domain.dtos.viescolaire;

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
public class ConvocationCreateDto {

    @NotNull(message = "Parent requis")
    private UUID parentId;

    @NotNull(message = "Élève requis")
    private UUID eleveId;

    @NotNull(message = "Motif requis")
    private String motif;

    @NotNull(message = "Date requise")
    private Instant dateConvocation;
}
