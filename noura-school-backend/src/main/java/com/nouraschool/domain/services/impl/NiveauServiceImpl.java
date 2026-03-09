package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.etablissement.NiveauDto;
import com.nouraschool.domain.entities.NiveauEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.NiveauRepository;
import com.nouraschool.domain.services.NiveauService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class NiveauServiceImpl implements NiveauService {

    @Inject
    NiveauRepository niveauRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<NiveauDto> findAll(Optional<UUID> cycleId) {
        UUID tenantId = requireTenantId();
        List<NiveauEntity> entities = cycleId.isPresent()
                ? niveauRepository.findByCycleId(cycleId.get())
                : niveauRepository.findByTenantId(tenantId);
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public NiveauDto findById(UUID id) {
        NiveauEntity entity = niveauRepository.findById(id);
        if (entity == null) throw new NotFoundException("Niveau not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public NiveauDto create(NiveauDto dto) {
        NiveauEntity entity = new NiveauEntity();
        entity.tenantId = requireTenantId();
        entity.cycleId = dto.getCycleId();
        entity.code = dto.getCode();
        entity.libelle = dto.getLibelle();
        entity.ordre = dto.getOrdre() != null ? dto.getOrdre() : 0;
        entity.actif = dto.getActif() != null ? dto.getActif() : true;
        return toDto(niveauRepository.persist(entity));
    }

    @Override
    @Transactional
    public NiveauDto update(UUID id, NiveauDto dto) {
        NiveauEntity entity = niveauRepository.findById(id);
        if (entity == null) throw new NotFoundException("Niveau not found: " + id);
        if (dto.getCycleId() != null) entity.cycleId = dto.getCycleId();
        if (dto.getCode() != null) entity.code = dto.getCode();
        if (dto.getLibelle() != null) entity.libelle = dto.getLibelle();
        if (dto.getOrdre() != null) entity.ordre = dto.getOrdre();
        if (dto.getActif() != null) entity.actif = dto.getActif();
        return toDto(niveauRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        NiveauEntity entity = niveauRepository.findById(id);
        if (entity == null) throw new NotFoundException("Niveau not found: " + id);
        niveauRepository.delete(entity);
    }

    private NiveauDto toDto(NiveauEntity e) {
        return NiveauDto.builder()
                .id(e.id)
                .cycleId(e.cycleId)
                .code(e.code)
                .libelle(e.libelle)
                .ordre(e.ordre)
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
