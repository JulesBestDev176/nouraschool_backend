package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.etablissement.AnnonceCreateDto;
import com.nouraschool.domain.dtos.etablissement.AnnonceDto;
import com.nouraschool.domain.entities.AnnonceEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.AnnonceRepository;
import com.nouraschool.domain.services.AnnonceService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AnnonceServiceImpl implements AnnonceService {

    @Inject
    AnnonceRepository annonceRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<AnnonceDto> findAll() {
        UUID tenantId = requireTenantId();
        return annonceRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AnnonceDto findById(UUID id) {
        AnnonceEntity entity = annonceRepository.findById(id);
        if (entity == null) throw new NotFoundException("Annonce not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public AnnonceDto create(AnnonceCreateDto dto) {
        AnnonceEntity entity = new AnnonceEntity();
        entity.tenantId = requireTenantId();
        entity.titre = dto.getTitre();
        entity.contenu = dto.getContenu();
        entity.dateDebut = dto.getDateDebut();
        entity.dateFin = dto.getDateFin();
        entity.actif = dto.getActif() != null ? dto.getActif() : true;
        return toDto(annonceRepository.persist(entity));
    }

    @Override
    @Transactional
    public AnnonceDto update(UUID id, AnnonceCreateDto dto) {
        AnnonceEntity entity = annonceRepository.findById(id);
        if (entity == null) throw new NotFoundException("Annonce not found: " + id);
        entity.titre = dto.getTitre();
        entity.contenu = dto.getContenu();
        entity.dateDebut = dto.getDateDebut();
        entity.dateFin = dto.getDateFin();
        if (dto.getActif() != null) entity.actif = dto.getActif();
        return toDto(annonceRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AnnonceEntity entity = annonceRepository.findById(id);
        if (entity == null) throw new NotFoundException("Annonce not found: " + id);
        annonceRepository.delete(entity);
    }

    private AnnonceDto toDto(AnnonceEntity e) {
        return AnnonceDto.builder()
                .id(e.id)
                .titre(e.titre)
                .contenu(e.contenu)
                .dateDebut(e.dateDebut)
                .dateFin(e.dateFin)
                .actif(e.actif)
                .createdAt(e.createdAt)
                .updatedAt(e.updatedAt)
                .build();
    }

    private UUID requireTenantId() {
        if (!tenantContext.hasTenant()) {
            throw new InvalidRequestException("Tenant context required");
        }
        return tenantContext.getTenantId();
    }
}
