package com.nouraschool.domain.dtos;

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
public class CahierTexteDto {

    private UUID id;
    private UUID coursId;
    private java.time.LocalDate dateCours;
    private String contenuTraite;
    private String observations;
    private String etapeProgramme;
    private Boolean programmeValide;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
