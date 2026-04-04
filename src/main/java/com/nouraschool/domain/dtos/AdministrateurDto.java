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
public class AdministrateurDto {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String telephone;
    private String adresse;
    private Boolean active;
    private String niveauAcces;
    private String droitsSpeciaux;
}
