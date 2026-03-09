package com.nouraschool.domain.dtos.rh;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelCreateDto {

    @NotNull(message = "Utilisateur requis")
    private UUID utilisateurId;

    private String numeroMatricule;
    private String typeContrat;
    private LocalDate dateEmbauche;
    private BigDecimal salaire;
    private Integer soldeConge;
}
