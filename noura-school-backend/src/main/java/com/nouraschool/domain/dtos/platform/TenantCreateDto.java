package com.nouraschool.domain.dtos.platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantCreateDto {
    @NotBlank(message = "Slug requis")
    @Size(max = 100)
    private String slug;

    @NotBlank(message = "Nom requis")
    @Size(max = 255)
    private String nom;

    @Size(max = 254)
    private String emailContact;

    @Size(max = 20)
    private String telephone;

    private String adresse;

    @Size(max = 500)
    private String logoUrl;

    @Size(max = 50)
    private String plan = "TRIAL";

    /** Email de l'admin initial (optionnel). Si absent : admin@{slug}.noura-school.local */
    @jakarta.validation.constraints.Email
    @Size(max = 254)
    private String initialAdminEmail;

    /** Mot de passe initial (optionnel). Si absent : mot de passe aléatoire + mustChangePassword=true */
    @Size(min = 8)
    private String initialAdminPassword;
}
