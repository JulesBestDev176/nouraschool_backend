package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.rh.PointageCreateDto;
import com.nouraschool.domain.dtos.rh.PointageDto;
import com.nouraschool.domain.dtos.rh.PointageRapportDto;
import com.nouraschool.domain.entities.PointageEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.PointageRepository;
import com.nouraschool.domain.services.PointageService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PointageServiceImpl implements PointageService {

    @Inject
    PointageRepository pointageRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<PointageDto> findAll() {
        UUID tenantId = requireTenantId();
        return pointageRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PointageDto findById(UUID id) {
        PointageEntity entity = pointageRepository.findById(id);
        if (entity == null) throw new NotFoundException("Pointage not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public PointageDto create(PointageCreateDto dto) {
        PointageEntity entity = new PointageEntity();
        entity.tenantId = requireTenantId();
        entity.utilisateurId = dto.getUtilisateurId();
        entity.typePointage = dto.getTypePointage();
        entity.dateHeure = dto.getDateHeure();
        entity.methode = dto.getMethode();
        entity.createdBy = dto.getCreatedBy();
        return toDto(pointageRepository.persist(entity));
    }

    @Override
    public PointageRapportDto getRapport(Instant debut, Instant fin) {
        UUID tenantId = requireTenantId();
        List<PointageEntity> pointages = pointageRepository.findByDateRange(tenantId, debut, fin);
        return PointageRapportDto.builder()
                .dateDebut(debut)
                .dateFin(fin)
                .pointages(pointages.stream().map(this::toDto).collect(Collectors.toList()))
                .build();
    }

    private PointageDto toDto(PointageEntity e) {
        return PointageDto.builder()
                .id(e.id)
                .utilisateurId(e.utilisateurId)
                .typePointage(e.typePointage)
                .dateHeure(e.dateHeure)
                .methode(e.methode)
                .createdBy(e.createdBy)
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
