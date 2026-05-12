package com.nouraschool.domain.dtos.platform;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantUpdateDto {
    @Size(max = 255)
    private String nom;

    @Size(max = 254)
    private String emailContact;

    @Size(max = 20)
    private String telephone;

    private String adresse;

    private String logoUrl;

    @Size(max = 50)
    private String plan;

    private Integer durationMonths;

    private Boolean actif;

    private LocalDate dateExpiration;
}
