package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.etablissement.SalleDto;
import com.nouraschool.domain.entities.SalleEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.SalleRepository;
import com.nouraschool.domain.services.SalleService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class SalleServiceImpl implements SalleService {

    @Inject
    SalleRepository salleRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<SalleDto> findAll(Optional<UUID> batimentId) {
        UUID tenantId = requireTenantId();
        List<SalleEntity> entities = batimentId.isPresent()
                ? salleRepository.findByBatimentId(batimentId.get())
                : salleRepository.findByTenantId(tenantId);
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public SalleDto findById(UUID id) {
        SalleEntity entity = salleRepository.findById(id);
        if (entity == null) throw new NotFoundException("Salle not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public SalleDto create(SalleDto dto) {
        SalleEntity entity = new SalleEntity();
        entity.tenantId = requireTenantId();
        entity.batimentId = dto.getBatimentId();
        entity.nom = dto.getNom();
        entity.capacite = dto.getCapacite();
        entity.typeSalle = dto.getTypeSalle();
        entity.actif = dto.getActif() != null ? dto.getActif() : true;
        return toDto(salleRepository.persist(entity));
    }

    @Override
    @Transactional
    public SalleDto update(UUID id, SalleDto dto) {
        SalleEntity entity = salleRepository.findById(id);
        if (entity == null) throw new NotFoundException("Salle not found: " + id);
        if (dto.getBatimentId() != null) entity.batimentId = dto.getBatimentId();
        if (dto.getNom() != null) entity.nom = dto.getNom();
        if (dto.getCapacite() != null) entity.capacite = dto.getCapacite();
        if (dto.getTypeSalle() != null) entity.typeSalle = dto.getTypeSalle();
        if (dto.getActif() != null) entity.actif = dto.getActif();
        return toDto(salleRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SalleEntity entity = salleRepository.findById(id);
        if (entity == null) throw new NotFoundException("Salle not found: " + id);
        salleRepository.delete(entity);
    }

    private SalleDto toDto(SalleEntity e) {
        return SalleDto.builder()
                .id(e.id)
                .batimentId(e.batimentId)
                .nom(e.nom)
                .capacite(e.capacite)
                .typeSalle(e.typeSalle)
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
