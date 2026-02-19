package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnseignantDto {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String telephone;
    private String adresse;
    private Boolean active;
    private String matricule;
    private String specialite;
    private LocalDate dateEmbauche;
    private String numeroCNPS;
    private String numeroSecuriteSociale;
}
