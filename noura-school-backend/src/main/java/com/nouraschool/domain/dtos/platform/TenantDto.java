package com.nouraschool.domain.dtos.platform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {
    private UUID id;
    private String slug;
    private String nom;
    private String emailContact;
    private String telephone;
    private String adresse;
    private String logoUrl;
    private String plan;
    private Boolean actif;
    private LocalDate dateExpiration;
    private Instant createdAt;
    private Instant updatedAt;
}
