package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.etablissement.AnneeAcademiqueDto;
import com.nouraschool.domain.entities.AnneeAcademiqueEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.AnneeAcademiqueRepository;
import com.nouraschool.domain.services.AnneeAcademiqueService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AnneeAcademiqueServiceImpl implements AnneeAcademiqueService {

    @Inject
    AnneeAcademiqueRepository anneeAcademiqueRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<AnneeAcademiqueDto> findAll() {
        UUID tenantId = requireTenantId();
        return anneeAcademiqueRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AnneeAcademiqueDto> findCourante() {
        UUID tenantId = requireTenantId();
        return anneeAcademiqueRepository.findCouranteByTenantId(tenantId).map(this::toDto);
    }

    @Override
    public AnneeAcademiqueDto findById(UUID id) {
        AnneeAcademiqueEntity entity = anneeAcademiqueRepository.findById(id);
        if (entity == null) throw new NotFoundException("AnneeAcademique not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public AnneeAcademiqueDto create(AnneeAcademiqueDto dto) {
        AnneeAcademiqueEntity entity = new AnneeAcademiqueEntity();
        entity.tenantId = requireTenantId();
        entity.libelle = dto.getLibelle();
        entity.dateDebut = dto.getDateDebut();
        entity.dateFin = dto.getDateFin();
        entity.estCourante = dto.getEstCourante() != null ? dto.getEstCourante() : false;
        entity.actif = dto.getActif() != null ? dto.getActif() : true;
        return toDto(anneeAcademiqueRepository.persist(entity));
    }

    @Override
    @Transactional
    public AnneeAcademiqueDto update(UUID id, AnneeAcademiqueDto dto) {
        AnneeAcademiqueEntity entity = anneeAcademiqueRepository.findById(id);
        if (entity == null) throw new NotFoundException("AnneeAcademique not found: " + id);
        if (dto.getLibelle() != null) entity.libelle = dto.getLibelle();
        if (dto.getDateDebut() != null) entity.dateDebut = dto.getDateDebut();
        if (dto.getDateFin() != null) entity.dateFin = dto.getDateFin();
        if (dto.getEstCourante() != null) entity.estCourante = dto.getEstCourante();
        if (dto.getActif() != null) entity.actif = dto.getActif();
        return toDto(anneeAcademiqueRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AnneeAcademiqueEntity entity = anneeAcademiqueRepository.findById(id);
        if (entity == null) throw new NotFoundException("AnneeAcademique not found: " + id);
        anneeAcademiqueRepository.delete(entity);
    }

    @Override
    @Transactional
    public AnneeAcademiqueDto activer(UUID id) {
        UUID tenantId = requireTenantId();
        AnneeAcademiqueEntity toActivate = anneeAcademiqueRepository.findById(id);
        if (toActivate == null) throw new NotFoundException("AnneeAcademique not found: " + id);
        for (AnneeAcademiqueEntity e : anneeAcademiqueRepository.findByTenantId(tenantId)) {
            e.estCourante = e.id.equals(id);
            anneeAcademiqueRepository.persist(e);
        }
        return toDto(toActivate);
    }

    private AnneeAcademiqueDto toDto(AnneeAcademiqueEntity e) {
        return AnneeAcademiqueDto.builder()
                .id(e.id)
                .libelle(e.libelle)
                .dateDebut(e.dateDebut)
                .dateFin(e.dateFin)
                .estCourante(e.estCourante)
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
