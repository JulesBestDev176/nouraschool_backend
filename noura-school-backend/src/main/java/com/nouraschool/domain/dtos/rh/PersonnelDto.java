package com.nouraschool.domain.dtos.rh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelDto {

    private UUID id;
    private UUID utilisateurId;
    private String numeroMatricule;
    private String typeContrat;
    private LocalDate dateEmbauche;
    private BigDecimal salaire;
    private Integer soldeConge;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
