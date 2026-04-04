package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/** Réponse GET /api/v1/auth/me (task.md UC-AUTH-07). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthMeDto {
    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private String tenantId;
    private List<String> cycles;
    private Boolean actif;
}
