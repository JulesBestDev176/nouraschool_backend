package com.nouraschool.domain.usecases.admin;

import com.nouraschool.domain.dtos.MatiereDto;
import com.nouraschool.domain.entities.MatiereEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.mappers.MatiereMapper;
import com.nouraschool.domain.repositories.MatiereRepository;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AdminMatiereUseCaseImpl implements AdminMatiereUseCase {

    @Inject
    MatiereRepository matiereRepository;
    @Inject
    MatiereMapper matiereMapper;
    @Inject
    TenantContext tenantContext;

    @Override
    public List<MatiereDto> findAll() {
        return matiereRepository.findAll().stream().map(matiereMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public MatiereDto findById(UUID id) {
        MatiereEntity entity = matiereRepository.findById(id);
        if (entity == null) throw new NotFoundException("Matiere not found: " + id);
        return matiereMapper.toDto(entity);
    }

    @Override
    @Transactional
    public MatiereDto create(MatiereDto dto) {
        MatiereEntity entity = matiereMapper.toEntity(dto);
        if (tenantContext.hasTenant()) entity.tenantId = tenantContext.getTenantId();
        return matiereMapper.toDto(matiereRepository.persist(entity));
    }

    @Override
    @Transactional
    public MatiereDto update(UUID id, MatiereDto dto) {
        MatiereEntity entity = matiereRepository.findById(id);
        if (entity == null) throw new NotFoundException("Matiere not found: " + id);
        entity.nom = dto.getNom();
        entity.code = dto.getCode();
        entity.description = dto.getDescription();
        entity.coefficient = dto.getCoefficient();
        entity.categorie = dto.getCategorie();
        return matiereMapper.toDto(matiereRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        MatiereEntity entity = matiereRepository.findById(id);
        if (entity == null) throw new NotFoundException("Matiere not found: " + id);
        matiereRepository.delete(entity);
    }
}
