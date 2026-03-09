package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.MatiereClasseDto;
import com.nouraschool.domain.entities.MatiereClasseEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.MatiereClasseMapper;
import com.nouraschool.domain.repositories.ClasseRepository;
import com.nouraschool.domain.repositories.EnseignantRepository;
import com.nouraschool.domain.repositories.MatiereClasseRepository;
import com.nouraschool.domain.repositories.MatiereRepository;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminMatiereClasseUseCaseImpl implements AdminMatiereClasseUseCase {

    @Inject
    MatiereClasseRepository matiereClasseRepository;
    @Inject
    MatiereRepository matiereRepository;
    @Inject
    ClasseRepository classeRepository;
    @Inject
    EnseignantRepository enseignantRepository;
    @Inject
    MatiereClasseMapper matiereClasseMapper;
    @Inject
    TenantContext tenantContext;

    @Override
    public List<MatiereClasseDto> findAll() {
        return matiereClasseRepository.findAll().stream().map(matiereClasseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public MatiereClasseDto findById(UUID id) {
        MatiereClasseEntity entity = matiereClasseRepository.findById(id);
        if (entity == null) throw new NotFoundException("MatiereClasse not found: " + id);
        return matiereClasseMapper.toDto(entity);
    }

    @Override
    @Transactional
    public MatiereClasseDto create(MatiereClasseDto dto) {
        MatiereClasseEntity entity = matiereClasseMapper.toEntity(dto);
        if (tenantContext.hasTenant()) entity.tenantId = tenantContext.getTenantId();
        if (dto.getMatiereId() != null) entity.matiere = matiereRepository.findById(dto.getMatiereId());
        if (dto.getClasseId() != null) entity.classe = classeRepository.findById(dto.getClasseId());
        if (dto.getEnseignantId() != null) entity.enseignantEntity = enseignantRepository.findById(dto.getEnseignantId());
        return matiereClasseMapper.toDto(matiereClasseRepository.persist(entity));
    }

    @Override
    @Transactional
    public MatiereClasseDto update(UUID id, MatiereClasseDto dto) {
        MatiereClasseEntity entity = matiereClasseRepository.findById(id);
        if (entity == null) throw new NotFoundException("MatiereClasse not found: " + id);
        if (dto.getMatiereId() != null) entity.matiere = matiereRepository.findById(dto.getMatiereId());
        if (dto.getClasseId() != null) entity.classe = classeRepository.findById(dto.getClasseId());
        if (dto.getEnseignantId() != null) entity.enseignantEntity = enseignantRepository.findById(dto.getEnseignantId());
        if (dto.getAnneeScolaire() != null) entity.anneeScolaire = dto.getAnneeScolaire();
        if (dto.getVolumeHoraire() != null) entity.volumeHoraire = dto.getVolumeHoraire();
        return matiereClasseMapper.toDto(matiereClasseRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        MatiereClasseEntity entity = matiereClasseRepository.findById(id);
        if (entity == null) throw new NotFoundException("MatiereClasse not found: " + id);
        matiereClasseRepository.delete(entity);
    }
}
