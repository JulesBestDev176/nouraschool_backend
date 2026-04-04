package com.nouraschool.domain.dtos.rh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointageRapportDto {

    private Instant dateDebut;
    private Instant dateFin;
    private List<PointageDto> pointages;
}
