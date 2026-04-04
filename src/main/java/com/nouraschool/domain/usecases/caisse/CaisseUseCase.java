package com.nouraschool.domain.usecases.caisse;

import com.nouraschool.domain.dtos.PaiementDto;
import com.nouraschool.domain.enums.StatutPaiement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CaisseUseCase {

    List<PaiementDto> listerPaiements();

    List<PaiementDto> listerPaiementsParStatut(StatutPaiement statut);

    List<PaiementDto> historiquePaiements(LocalDateTime debut, LocalDateTime fin);

    PaiementDto consulterPaiement(UUID id);

    PaiementDto validerPaiement(UUID id);

    PaiementDto validerPaiement(UUID id, UUID caissierUserId);
}
