package com.nouraschool.domain.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nouraschool.domain.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EleveCreateDto {

    private String username;
    private String email;
    private String password;
    @JsonAlias({"prenom"})
    private String firstName;
    @JsonAlias({"nom"})
    private String lastName;
    private String telephone;
    private String adresse;
    private String matricule;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    @JsonAlias({"sexe"})
    private Genre genre;
    private String numeroUrgence;
    private LocalDate dateInscription;
    private String photoUrl;
    private UUID classeId;
    private List<UUID> parentIds;
}
