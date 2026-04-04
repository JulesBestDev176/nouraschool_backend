package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.rh.AbsencePersonnelCreateDto;
import com.nouraschool.domain.dtos.rh.AbsencePersonnelDto;
import com.nouraschool.domain.entities.AbsencePersonnelEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.AbsencePersonnelRepository;
import com.nouraschool.domain.services.AbsencePersonnelService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AbsencePersonnelServiceImpl implements AbsencePersonnelService {

    private static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    private static final String STATUT_VALIDE = "VALIDE";
    private static final String STATUT_REFUSE = "REFUSE";

    @Inject
    AbsencePersonnelRepository absencePersonnelRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<AbsencePersonnelDto> findAll() {
        UUID tenantId = requireTenantId();
        return absencePersonnelRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AbsencePersonnelDto findById(UUID id) {
        AbsencePersonnelEntity entity = absencePersonnelRepository.findById(id);
        if (entity == null) throw new NotFoundException("Absence personnel not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public AbsencePersonnelDto create(AbsencePersonnelCreateDto dto) {
        AbsencePersonnelEntity entity = new AbsencePersonnelEntity();
        entity.tenantId = requireTenantId();
        entity.utilisateurId = dto.getUtilisateurId();
        entity.dateDebut = dto.getDateDebut();
        entity.dateFin = dto.getDateFin();
        entity.motif = dto.getMotif();
        entity.typeAbsence = dto.getTypeAbsence();
        entity.justificatifUrl = dto.getJustificatifUrl();
        entity.statut = STATUT_EN_ATTENTE;
        return toDto(absencePersonnelRepository.persist(entity));
    }

    @Override
    @Transactional
    public AbsencePersonnelDto valider(UUID id, UUID validatedBy) {
        AbsencePersonnelEntity entity = absencePersonnelRepository.findById(id);
        if (entity == null) throw new NotFoundException("Absence personnel not found: " + id);
        if (!STATUT_EN_ATTENTE.equals(entity.statut)) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        entity.statut = STATUT_VALIDE;
        entity.validePar = validatedBy;
        return toDto(absencePersonnelRepository.persist(entity));
    }

    @Override
    @Transactional
    public AbsencePersonnelDto refuser(UUID id, UUID validatedBy) {
        AbsencePersonnelEntity entity = absencePersonnelRepository.findById(id);
        if (entity == null) throw new NotFoundException("Absence personnel not found: " + id);
        if (!STATUT_EN_ATTENTE.equals(entity.statut)) {
            throw new InvalidRequestException("REGLE_METIER_VIOLEE");
        }
        entity.statut = STATUT_REFUSE;
        entity.validePar = validatedBy;
        return toDto(absencePersonnelRepository.persist(entity));
    }

    private AbsencePersonnelDto toDto(AbsencePersonnelEntity e) {
        return AbsencePersonnelDto.builder()
                .id(e.id)
                .utilisateurId(e.utilisateurId)
                .dateDebut(e.dateDebut)
                .dateFin(e.dateFin)
                .motif(e.motif)
                .typeAbsence(e.typeAbsence)
                .justificatifUrl(e.justificatifUrl)
                .statut(e.statut)
                .validePar(e.validePar)
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
