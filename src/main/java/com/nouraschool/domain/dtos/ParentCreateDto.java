package com.nouraschool.domain.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nouraschool.domain.enums.LienParente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCreateDto {

    private String username;
    private String email;
    private String password;
    @JsonAlias({"prenom"})
    private String firstName;
    @JsonAlias({"nom"})
    private String lastName;
    private String telephone;
    private String adresse;
    private String profession;
    private String lieuTravail;
    private String telephoneTravail;
    private LienParente lienParente;
}
