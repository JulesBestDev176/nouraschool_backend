package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.AbsenceEleveDto;
import com.nouraschool.domain.entities.AbsenceEleveEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.AbsenceEleveMapper;
import com.nouraschool.domain.repositories.AbsenceEleveRepository;
import com.nouraschool.domain.repositories.EleveRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminAbsenceEleveUseCaseImpl implements AdminAbsenceEleveUseCase {

    @Inject
    AbsenceEleveRepository absenceEleveRepository;
    @Inject
    EleveRepository eleveRepository;
    @Inject
    AbsenceEleveMapper absenceEleveMapper;

    @Override
    public List<AbsenceEleveDto> findAll() {
        return absenceEleveRepository.findAll().stream().map(absenceEleveMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AbsenceEleveDto findById(UUID id) {
        AbsenceEleveEntity entity = absenceEleveRepository.findById(id);
        if (entity == null) throw new NotFoundException("AbsenceEleve not found: " + id);
        return absenceEleveMapper.toDto(entity);
    }

    @Override
    @Transactional
    public AbsenceEleveDto create(AbsenceEleveDto dto) {
        AbsenceEleveEntity entity = absenceEleveMapper.toEntity(dto);
        if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
        return absenceEleveMapper.toDto(absenceEleveRepository.persist(entity));
    }

    @Override
    @Transactional
    public AbsenceEleveDto update(UUID id, AbsenceEleveDto dto) {
        AbsenceEleveEntity entity = absenceEleveRepository.findById(id);
        if (entity == null) throw new NotFoundException("AbsenceEleve not found: " + id);
        if (dto.getEleveId() != null) entity.eleve = eleveRepository.findById(dto.getEleveId());
        if (dto.getDate() != null) entity.date = dto.getDate();
        if (dto.getTypeAbsence() != null) entity.typeAbsence = dto.getTypeAbsence();
        if (dto.getJustifiee() != null) entity.justifiee = dto.getJustifiee();
        if (dto.getMotif() != null) entity.motif = dto.getMotif();
        if (dto.getDocumentJustificatifUrl() != null) entity.documentJustificatifUrl = dto.getDocumentJustificatifUrl();
        if (dto.getDeclaredBy() != null) entity.declaredBy = dto.getDeclaredBy();
        return absenceEleveMapper.toDto(absenceEleveRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AbsenceEleveEntity entity = absenceEleveRepository.findById(id);
        if (entity == null) throw new NotFoundException("AbsenceEleve not found: " + id);
        absenceEleveRepository.delete(entity);
    }

    @Override
    @Transactional
    public AbsenceEleveDto approuver(UUID id, UUID approuvePar) {
        AbsenceEleveEntity entity = absenceEleveRepository.findById(id);
        if (entity == null) throw new NotFoundException("AbsenceEleve not found: " + id);
        if (!"EN_ATTENTE".equals(entity.statut)) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        entity.statut = "APPROUVEE";
        entity.approuvePar = approuvePar;
        return absenceEleveMapper.toDto(absenceEleveRepository.persist(entity));
    }

    @Override
    @Transactional
    public AbsenceEleveDto rejeter(UUID id, UUID approuvePar) {
        AbsenceEleveEntity entity = absenceEleveRepository.findById(id);
        if (entity == null) throw new NotFoundException("AbsenceEleve not found: " + id);
        if (!"EN_ATTENTE".equals(entity.statut)) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        entity.statut = "REJETEE";
        entity.approuvePar = approuvePar;
        return absenceEleveMapper.toDto(absenceEleveRepository.persist(entity));
    }
}
