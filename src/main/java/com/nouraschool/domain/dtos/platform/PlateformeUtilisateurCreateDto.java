package com.nouraschool.domain.dtos.platform;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlateformeUtilisateurCreateDto {
    @NotBlank(message = "Nom requis")
    @Size(max = 100)
    private String nom;

    @NotBlank(message = "Prénom requis")
    @Size(max = 100)
    private String prenom;

    @NotBlank(message = "Email requis")
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank(message = "Mot de passe requis")
    @Size(min = 8)
    private String motDePasse;

    @NotBlank(message = "Rôle plateforme requis (SUPER_ADMIN | GESTIONNAIRE)")
    @Size(max = 50)
    private String rolePlateforme;
}
