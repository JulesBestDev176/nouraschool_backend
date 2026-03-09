package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.viescolaire.ConvocationCreateDto;
import com.nouraschool.domain.dtos.viescolaire.ConvocationDto;
import com.nouraschool.domain.entities.ConvocationEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.ConvocationRepository;
import com.nouraschool.domain.repositories.EleveRepository;
import com.nouraschool.domain.repositories.ParentRepository;
import com.nouraschool.domain.services.ConvocationService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConvocationServiceImpl implements ConvocationService {

    @Inject
    ConvocationRepository convocationRepository;

    @Inject
    ParentRepository parentRepository;

    @Inject
    EleveRepository eleveRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<ConvocationDto> findAll() {
        UUID tenantId = requireTenantId();
        return convocationRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ConvocationDto findById(UUID id) {
        ConvocationEntity entity = convocationRepository.findById(id);
        if (entity == null) throw new NotFoundException("Convocation not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public ConvocationDto create(ConvocationCreateDto dto, UUID creePar) {
        UUID tenantId = requireTenantId();

        if (parentRepository.findById(dto.getParentId()) == null) {
            throw new NotFoundException("Parent introuvable: " + dto.getParentId());
        }
        if (eleveRepository.findById(dto.getEleveId()) == null) {
            throw new NotFoundException("Élève introuvable: " + dto.getEleveId());
        }

        ConvocationEntity entity = new ConvocationEntity();
        entity.tenantId = tenantId;
        entity.parent = parentRepository.findById(dto.getParentId());
        entity.eleve = eleveRepository.findById(dto.getEleveId());
        entity.motif = dto.getMotif();
        entity.dateConvocation = dto.getDateConvocation();
        entity.statut = "EN_ATTENTE";
        entity.creePar = creePar;

        return toDto(convocationRepository.persist(entity));
    }

    @Override
    @Transactional
    public ConvocationDto updateCompteRendu(UUID id, String compteRendu) {
        ConvocationEntity entity = convocationRepository.findById(id);
        if (entity == null) throw new NotFoundException("Convocation not found: " + id);
        entity.compteRendu = compteRendu;
        return toDto(convocationRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ConvocationEntity entity = convocationRepository.findById(id);
        if (entity == null) throw new NotFoundException("Convocation not found: " + id);
        convocationRepository.delete(entity);
    }

    private ConvocationDto toDto(ConvocationEntity e) {
        return ConvocationDto.builder()
                .id(e.id)
                .parentId(e.parent != null ? e.parent.id : null)
                .eleveId(e.eleve != null ? e.eleve.id : null)
                .motif(e.motif)
                .dateConvocation(e.dateConvocation)
                .statut(e.statut)
                .compteRendu(e.compteRendu)
                .creePar(e.creePar)
                .build();
    }

    private UUID requireTenantId() {
        if (!tenantContext.hasTenant()) {
            throw new InvalidRequestException("Tenant context required");
        }
        return tenantContext.getTenantId();
    }
}
