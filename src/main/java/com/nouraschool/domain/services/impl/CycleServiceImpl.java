package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.etablissement.CycleDto;
import com.nouraschool.domain.entities.CycleEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.CycleRepository;
import com.nouraschool.domain.services.CycleService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CycleServiceImpl implements CycleService {

    @Inject
    CycleRepository cycleRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<CycleDto> findAll() {
        UUID tenantId = requireTenantId();
        return cycleRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CycleDto findById(UUID id) {
        CycleEntity entity = cycleRepository.findById(id);
        if (entity == null) throw new NotFoundException("Cycle not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public CycleDto create(CycleDto dto) {
        CycleEntity entity = new CycleEntity();
        entity.tenantId = requireTenantId();
        entity.code = dto.getCode();
        entity.libelle = dto.getLibelle();
        entity.actif = dto.getActif() != null ? dto.getActif() : true;
        return toDto(cycleRepository.persist(entity));
    }

    @Override
    @Transactional
    public CycleDto update(UUID id, CycleDto dto) {
        CycleEntity entity = cycleRepository.findById(id);
        if (entity == null) throw new NotFoundException("Cycle not found: " + id);
        entity.code = dto.getCode();
        entity.libelle = dto.getLibelle();
        if (dto.getActif() != null) entity.actif = dto.getActif();
        return toDto(cycleRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CycleEntity entity = cycleRepository.findById(id);
        if (entity == null) throw new NotFoundException("Cycle not found: " + id);
        cycleRepository.delete(entity);
    }

    private CycleDto toDto(CycleEntity e) {
        return CycleDto.builder()
                .id(e.id)
                .code(e.code)
                .libelle(e.libelle)
                .actif(e.actif)
                .build();
    }

    private UUID requireTenantId() {
        if (!tenantContext.hasTenant()) {
            throw new InvalidRequestException("Tenant context required");
        }
        return tenantContext.getTenantId();
    }
}
