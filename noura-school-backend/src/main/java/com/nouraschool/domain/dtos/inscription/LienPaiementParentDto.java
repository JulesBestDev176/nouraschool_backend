package com.nouraschool.domain.dtos.inscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LienPaiementParentDto {

    private UUID id;
    private UUID parentId;
    private String token;
    private BigDecimal montantTotal;
    private String mois;
    private String statut;
    private Instant expiresAt;
    private Boolean otpVerified;
    private Instant createdAt;
}
