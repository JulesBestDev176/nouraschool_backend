package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.dtos.rh.PersonnelCreateDto;
import com.nouraschool.domain.dtos.rh.PersonnelDto;
import com.nouraschool.domain.entities.PersonnelEntity;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.PersonnelRepository;
import com.nouraschool.domain.services.PersonnelService;
import com.nouraschool.runtime.tenant.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PersonnelServiceImpl implements PersonnelService {

    @Inject
    PersonnelRepository personnelRepository;

    @Inject
    TenantContext tenantContext;

    @Override
    public List<PersonnelDto> findAll() {
        UUID tenantId = requireTenantId();
        return personnelRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PersonnelDto findById(UUID id) {
        PersonnelEntity entity = personnelRepository.findById(id);
        if (entity == null) throw new NotFoundException("Personnel not found: " + id);
        return toDto(entity);
    }

    @Override
    @Transactional
    public PersonnelDto create(PersonnelCreateDto dto) {
        PersonnelEntity entity = new PersonnelEntity();
        entity.tenantId = requireTenantId();
        entity.utilisateurId = dto.getUtilisateurId();
        entity.numeroMatricule = dto.getNumeroMatricule();
        entity.typeContrat = dto.getTypeContrat();
        entity.dateEmbauche = dto.getDateEmbauche();
        entity.salaire = dto.getSalaire();
        entity.soldeConge = dto.getSoldeConge() != null ? dto.getSoldeConge() : 0;
        return toDto(personnelRepository.persist(entity));
    }

    @Override
    @Transactional
    public PersonnelDto update(UUID id, PersonnelCreateDto dto) {
        PersonnelEntity entity = personnelRepository.findById(id);
        if (entity == null) throw new NotFoundException("Personnel not found: " + id);
        entity.utilisateurId = dto.getUtilisateurId();
        entity.numeroMatricule = dto.getNumeroMatricule();
        entity.typeContrat = dto.getTypeContrat();
        entity.dateEmbauche = dto.getDateEmbauche();
        entity.salaire = dto.getSalaire();
        if (dto.getSoldeConge() != null) entity.soldeConge = dto.getSoldeConge();
        return toDto(personnelRepository.persist(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PersonnelEntity entity = personnelRepository.findById(id);
        if (entity == null) throw new NotFoundException("Personnel not found: " + id);
        personnelRepository.delete(entity);
    }

    private PersonnelDto toDto(PersonnelEntity e) {
        return PersonnelDto.builder()
                .id(e.id)
                .utilisateurId(e.utilisateurId)
                .numeroMatricule(e.numeroMatricule)
                .typeContrat(e.typeContrat)
                .dateEmbauche(e.dateEmbauche)
                .salaire(e.salaire)
                .soldeConge(e.soldeConge)
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
