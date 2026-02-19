package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.PaiementDto;
import com.nouraschool.domain.entities.PaiementEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.PaiementMapper;
import com.nouraschool.domain.repositories.EleveRepository;
import com.nouraschool.domain.repositories.PaiementRepository;
import com.nouraschool.domain.repositories.ParentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminPaiementUseCaseImpl implements AdminPaiementUseCase {

    @Inject
    PaiementRepository paiementRepository;
    @Inject
    EleveRepository eleveRepository;
    @Inject
    ParentRepository parentRepository;
    @Inject
    PaiementMapper paiementMapper;

    @Override
    public List<PaiementDto> findAll() {
        return paiementRepository.findAll().stream().map(paiementMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public PaiementDto findById(UUID id) {
        PaiementEntity entity = paiementRepository.findById(id);
        if (entity == null) throw new NotFoundException("Paiement not found: " + id);
        return paiementMapper.toDto(entity);
    }

    @Override
    @Transactional
    public PaiementDto create(PaiementDto dto) {
        PaiementEntity entity = paiementMapper.toEntity(dto);
        if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
        if (dto.getParentId() != null) entity.parent = parentRepository.findById(dto.getParentId());
        return paiementMapper.toDto(paiementRepository.persist(entity));
    }

    @Override
    @Transactional
    public PaiementDto update(UUID id, PaiementDto dto) {
        PaiementEntity entity = paiementRepository.findById(id);
        if (entity == null) throw new NotFoundException("Paiement not found: " + id);
        if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
        if (dto.getParentId() != null) entity.parent = parentRepository.findById(dto.getParentId());
        if (dto.getReference() != null) entity.reference = dto.getReference();
        if (dto.getMontant() != null) entity.montant = dto.getMontant();
        if (dto.getTypePaiement() != null) entity.typePaiement = dto.getTypePaiement();
        if (dto.getModePaiement() != null) entity.modePaiement = dto.getModePaiement();
        if (dto.getStatut() != null) entity.statut = dto.getStatut();
        if (dto.getAnneeScolaire() != null) entity.anneeScolaire = dto.getAnneeScolaire();
        if (dto.getTrimestre() != null) entity.trimestre = dto.getTrimestre();
        if (dto.getTransactionId() != null) entity.transactionId = dto.getTransactionId();
        if (dto.getDescription() != null) entity.description = dto.getDescription();
        if (dto.getDatePaiement() != null) entity.datePaiement = dto.getDatePaiement();
        if (dto.getValidePar() != null) entity.validePar = dto.getValidePar();
        return paiementMapper.toDto(paiementRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PaiementEntity entity = paiementRepository.findById(id);
        if (entity == null) throw new NotFoundException("Paiement not found: " + id);
        paiementRepository.delete(entity);
    }
}
