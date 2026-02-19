package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnseignantCreateDto {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String telephone;
    private String adresse;
    private String matricule;
    private String specialite;
    private LocalDate dateEmbauche;
    private String numeroCNPS;
    private String numeroSecuriteSociale;
}
