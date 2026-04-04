package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatiereDto {

    private UUID id;
    private String nom;
    private String code;
    private String description;
    private Integer coefficient;
    private String categorie;
}
