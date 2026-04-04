package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.AbsenceEnseignantDto;
import com.nouraschool.domain.entities.AbsenceEnseignantEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.AbsenceEnseignantMapper;
import com.nouraschool.domain.repositories.AbsenceEnseignantRepository;
import com.nouraschool.domain.repositories.EnseignantRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminAbsenceEnseignantUseCaseImpl implements AdminAbsenceEnseignantUseCase {

    @Inject
    AbsenceEnseignantRepository absenceEnseignantRepository;
    @Inject
    EnseignantRepository enseignantRepository;
    @Inject
    AbsenceEnseignantMapper absenceEnseignantMapper;

    @Override
    public List<AbsenceEnseignantDto> findAll() {
        return absenceEnseignantRepository.findAll().stream().map(absenceEnseignantMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AbsenceEnseignantDto findById(UUID id) {
        AbsenceEnseignantEntity entity = absenceEnseignantRepository.findById(id);
        if (entity == null) throw new NotFoundException("AbsenceEnseignant not found: " + id);
        return absenceEnseignantMapper.toDto(entity);
    }

    @Override
    @Transactional
    public AbsenceEnseignantDto create(AbsenceEnseignantDto dto) {
        AbsenceEnseignantEntity entity = absenceEnseignantMapper.toEntity(dto);
        if (dto.getEnseignantId() != null) entity.enseignantEntity = enseignantRepository.findById(dto.getEnseignantId());
        return absenceEnseignantMapper.toDto(absenceEnseignantRepository.persist(entity));
    }

    @Override
    @Transactional
    public AbsenceEnseignantDto update(UUID id, AbsenceEnseignantDto dto) {
        AbsenceEnseignantEntity entity = absenceEnseignantRepository.findById(id);
        if (entity == null) throw new NotFoundException("AbsenceEnseignant not found: " + id);
        if (dto.getEnseignantId() != null) entity.enseignantEntity = enseignantRepository.findById(dto.getEnseignantId());
        if (dto.getDateDebut() != null) entity.dateDebut = dto.getDateDebut();
        if (dto.getDateFin() != null) entity.dateFin = dto.getDateFin();
        if (dto.getMotif() != null) entity.motif = dto.getMotif();
        if (dto.getJustifiee() != null) entity.justifiee = dto.getJustifiee();
        if (dto.getDocumentJustificatifUrl() != null) entity.documentJustificatifUrl = dto.getDocumentJustificatifUrl();
        if (dto.getRemplacantId() != null) entity.remplacantId = dto.getRemplacantId();
        if (dto.getNotificationEnvoyee() != null) entity.notificationEnvoyee = dto.getNotificationEnvoyee();
        return absenceEnseignantMapper.toDto(absenceEnseignantRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AbsenceEnseignantEntity entity = absenceEnseignantRepository.findById(id);
        if (entity == null) throw new NotFoundException("AbsenceEnseignant not found: " + id);
        absenceEnseignantRepository.delete(entity);
    }
}
