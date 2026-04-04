package com.nouraschool.domain.dtos;

import com.nouraschool.domain.enums.ModePaiement;
import com.nouraschool.domain.enums.StatutPaiement;
import com.nouraschool.domain.enums.TypePaiement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDto {

    private UUID id;
    private UUID eleveId;
    private UUID parentId;
    private String reference;
    private Double montant;
    private TypePaiement typePaiement;
    private ModePaiement modePaiement;
    private StatutPaiement statut;
    private String anneeScolaire;
    private String trimestre;
    private String transactionId;
    private String description;
    private LocalDateTime datePaiement;
    private UUID validePar;
    private LocalDateTime createdAt;
}
