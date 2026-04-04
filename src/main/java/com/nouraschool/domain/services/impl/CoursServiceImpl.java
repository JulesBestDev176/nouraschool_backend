package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.etablissement.CoursCreateDto;
import com.nouraschool.domain.dtos.etablissement.CoursDto;
import com.nouraschool.domain.entities.CoursEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.CoursRepository;
import com.nouraschool.domain.services.CoursService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CoursServiceImpl implements CoursService {

    @Inject
    CoursRepository coursRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<CoursDto> findAll(Optional<UUID> classeId) {
        UUID tenantId = requireTenantId();
        List<CoursEntity> entities = classeId.isPresent()
                ? coursRepository.findByClasseId(classeId.get())
                : coursRepository.findByTenantId(tenantId);
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public CoursDto findById(UUID id) {
        CoursEntity entity = coursRepository.findById(id);
        if (entity == null) throw new NotFoundException("Cours not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public CoursDto create(CoursCreateDto dto) {
        CoursEntity entity = new CoursEntity();
        entity.tenantId = requireTenantId();
        entity.matiereId = dto.getMatiereId();
        entity.professeurId = dto.getProfesseurId();
        entity.classeId = dto.getClasseId();
        entity.anneeAcademiqueId = dto.getAnneeAcademiqueId();
        entity.volumeHoraireHebdo = dto.getVolumeHoraireHebdo();
        entity.coefficient = dto.getCoefficient();
        return toDto(coursRepository.persist(entity));
    }

    @Override
    @Transactional
    public CoursDto update(UUID id, CoursDto dto) {
        CoursEntity entity = coursRepository.findById(id);
        if (entity == null) throw new NotFoundException("Cours not found: " + id);
        if (dto.getMatiereId() != null) entity.matiereId = dto.getMatiereId();
        if (dto.getProfesseurId() != null) entity.professeurId = dto.getProfesseurId();
        if (dto.getClasseId() != null) entity.classeId = dto.getClasseId();
        if (dto.getAnneeAcademiqueId() != null) entity.anneeAcademiqueId = dto.getAnneeAcademiqueId();
        if (dto.getVolumeHoraireHebdo() != null) entity.volumeHoraireHebdo = dto.getVolumeHoraireHebdo();
        if (dto.getCoefficient() != null) entity.coefficient = dto.getCoefficient();
        return toDto(coursRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CoursEntity entity = coursRepository.findById(id);
        if (entity == null) throw new NotFoundException("Cours not found: " + id);
        coursRepository.delete(entity);
    }

    private CoursDto toDto(CoursEntity e) {
        return CoursDto.builder()
                .id(e.id)
                .matiereId(e.matiereId)
                .professeurId(e.professeurId)
                .classeId(e.classeId)
                .anneeAcademiqueId(e.anneeAcademiqueId)
                .volumeHoraireHebdo(e.volumeHoraireHebdo)
                .coefficient(e.coefficient)
                .build();
    }

    private UUID requireTenantId() {
        if (!tenantContext.hasTenant()) {
            throw new InvalidRequestException("Tenant context required");
        }
        return tenantContext.getTenantId();
    }
}
