package com.nouraschool.domain.usecases.caisse;

import com.nouraschool.domain.dtos.PaiementDto;
import com.nouraschool.domain.entities.PaiementEntity;
import com.nouraschool.domain.enums.StatutPaiement;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.PaiementMapper;
import com.nouraschool.domain.repositories.PaiementRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CaisseUseCaseImpl implements CaisseUseCase {

    @Inject
    PaiementRepository paiementRepository;
    @Inject
    PaiementMapper paiementMapper;

    @Override
    public List<PaiementDto> listerPaiements() {
        return paiementRepository.findAll().stream().map(paiementMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PaiementDto> listerPaiementsParStatut(StatutPaiement statut) {
        var list = PaiementEntity.list("statut", statut);
        return list.stream().map(pe -> paiementMapper.toDto((PaiementEntity) pe)).collect(Collectors.toList());
    }

    @Override
    public List<PaiementDto> historiquePaiements(LocalDateTime debut, LocalDateTime fin) {
        return paiementRepository.findBetweenDates(debut, fin).stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaiementDto consulterPaiement(UUID id) {
        var entity = paiementRepository.findById(id);
        if (entity == null) throw new NotFoundException("Paiement non trouvé: " + id);
        return paiementMapper.toDto(entity);
    }

    @Override
    @Transactional
    public PaiementDto validerPaiement(UUID id) {
        return validerPaiement(id, null);
    }

    @Override
    @Transactional
    public PaiementDto validerPaiement(UUID id, UUID caissierUserId) {
        var entity = paiementRepository.findById(id);
        if (entity == null) throw new NotFoundException("Paiement non trouvé: " + id);
        entity.statut = StatutPaiement.VALIDE;
        entity.datePaiement = LocalDateTime.now();
        if (caissierUserId != null) entity.validePar = caissierUserId;
        return paiementMapper.toDto(paiementRepository.persist(entity));
    }
}
