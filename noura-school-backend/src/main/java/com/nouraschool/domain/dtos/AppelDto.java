package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppelDto {

    private UUID id;
    private UUID coursId;
    private LocalDate dateCours;
    private LocalTime heureDebut;
    private String statut;
    private UUID soumisPar;
    private LocalDateTime createdAt;
    private List<AppelLigneDto> lignes;
}
