package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.PersonnelEntity;
import com.nouraschool.domain.repositories.PersonnelRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PersonnelRepositoryImpl implements PersonnelRepository {

    @Override
    public List<PersonnelEntity> findByTenantId(UUID tenantId) {
        return PersonnelEntity.list("tenantId", tenantId);
    }

    @Override
    public PersonnelEntity findById(UUID id) {
        return PersonnelEntity.findById(id);
    }

    @Override
    public List<PersonnelEntity> findByUtilisateurId(UUID utilisateurId) {
        return PersonnelEntity.list("utilisateurId", utilisateurId);
    }

    @Override
    public PersonnelEntity persist(PersonnelEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(PersonnelEntity entity) {
        entity.delete();
    }
}
