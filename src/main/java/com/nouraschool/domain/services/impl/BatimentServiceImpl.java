package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.etablissement.BatimentDto;
import com.nouraschool.domain.entities.BatimentEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.BatimentRepository;
import com.nouraschool.domain.services.BatimentService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class BatimentServiceImpl implements BatimentService {

    @Inject
    BatimentRepository batimentRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<BatimentDto> findAll() {
        UUID tenantId = requireTenantId();
        return batimentRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BatimentDto findById(UUID id) {
        BatimentEntity entity = batimentRepository.findById(id);
        if (entity == null) throw new NotFoundException("Batiment not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public BatimentDto create(BatimentDto dto) {
        BatimentEntity entity = new BatimentEntity();
        entity.tenantId = requireTenantId();
        entity.nom = dto.getNom();
        entity.description = dto.getDescription();
        entity.actif = dto.getActif() != null ? dto.getActif() : true;
        return toDto(batimentRepository.persist(entity));
    }

    @Override
    @Transactional
    public BatimentDto update(UUID id, BatimentDto dto) {
        BatimentEntity entity = batimentRepository.findById(id);
        if (entity == null) throw new NotFoundException("Batiment not found: " + id);
        if (dto.getNom() != null) entity.nom = dto.getNom();
        if (dto.getDescription() != null) entity.description = dto.getDescription();
        if (dto.getActif() != null) entity.actif = dto.getActif();
        return toDto(batimentRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        BatimentEntity entity = batimentRepository.findById(id);
        if (entity == null) throw new NotFoundException("Batiment not found: " + id);
        batimentRepository.delete(entity);
    }

    private BatimentDto toDto(BatimentEntity e) {
        return BatimentDto.builder()
                .id(e.id)
                .nom(e.nom)
                .description(e.description)
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
