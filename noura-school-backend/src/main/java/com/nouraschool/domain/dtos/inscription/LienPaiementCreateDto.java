package com.nouraschool.domain.dtos.inscription;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LienPaiementCreateDto {

    @NotNull(message = "Parent requis")
    private UUID parentId;

    @NotNull(message = "Montant requis")
    @DecimalMin(value = "0.01")
    private BigDecimal montantTotal;

    @NotBlank(message = "Mois requis")
    private String mois;
}
