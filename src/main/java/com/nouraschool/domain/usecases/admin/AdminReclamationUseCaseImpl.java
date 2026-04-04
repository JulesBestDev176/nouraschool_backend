package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.ReclamationDto;
import com.nouraschool.domain.entities.ReclamationEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.ReclamationMapper;
import com.nouraschool.domain.repositories.AbsenceEleveRepository;
import com.nouraschool.domain.repositories.EleveRepository;
import com.nouraschool.domain.repositories.NoteRepository;
import com.nouraschool.domain.repositories.ReclamationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminReclamationUseCaseImpl implements AdminReclamationUseCase {

    @Inject
    ReclamationRepository reclamationRepository;
    @Inject
    EleveRepository eleveRepository;
    @Inject
    NoteRepository noteRepository;
    @Inject
    AbsenceEleveRepository absenceEleveRepository;
    @Inject
    ReclamationMapper reclamationMapper;

    @Override
    public List<ReclamationDto> findAll() {
        return reclamationRepository.findAll().stream().map(reclamationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ReclamationDto findById(UUID id) {
        ReclamationEntity entity = reclamationRepository.findById(id);
        if (entity == null) throw new NotFoundException("Reclamation not found: " + id);
        return reclamationMapper.toDto(entity);
    }

    @Override
    @Transactional
    public ReclamationDto create(ReclamationDto dto) {
        ReclamationEntity entity = reclamationMapper.toEntity(dto);
        if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
        if (dto.getNoteId() != null) entity.note = noteRepository.findById(dto.getNoteId());
        if (dto.getAbsenceId() != null) entity.absence = absenceEleveRepository.findById(dto.getAbsenceId());
        return reclamationMapper.toDto(reclamationRepository.persist(entity));
    }

    @Override
    @Transactional
    public ReclamationDto update(UUID id, ReclamationDto dto) {
        ReclamationEntity entity = reclamationRepository.findById(id);
        if (entity == null) throw new NotFoundException("Reclamation not found: " + id);
        if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
        if (dto.getNoteId() != null) entity.note = noteRepository.findById(dto.getNoteId());
        if (dto.getAbsenceId() != null) entity.absence = absenceEleveRepository.findById(dto.getAbsenceId());
        if (dto.getTypeReclamation() != null) entity.typeReclamation = dto.getTypeReclamation();
        if (dto.getObjet() != null) entity.objet = dto.getObjet();
        if (dto.getDescription() != null) entity.description = dto.getDescription();
        if (dto.getStatut() != null) entity.statut = dto.getStatut();
        if (dto.getReponse() != null) entity.reponse = dto.getReponse();
        if (dto.getTraitePar() != null) entity.traitePar = dto.getTraitePar();
        return reclamationMapper.toDto(reclamationRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ReclamationEntity entity = reclamationRepository.findById(id);
        if (entity == null) throw new NotFoundException("Reclamation not found: " + id);
        reclamationRepository.delete(entity);
    }
}
