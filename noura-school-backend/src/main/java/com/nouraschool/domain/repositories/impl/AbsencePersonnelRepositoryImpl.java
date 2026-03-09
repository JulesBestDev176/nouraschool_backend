package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.AbsencePersonnelEntity;
import com.nouraschool.domain.repositories.AbsencePersonnelRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AbsencePersonnelRepositoryImpl implements AbsencePersonnelRepository {

    @Override
    public List<AbsencePersonnelEntity> findByTenantId(UUID tenantId) {
        return AbsencePersonnelEntity.list("tenantId", tenantId);
    }

    @Override
    public AbsencePersonnelEntity findById(UUID id) {
        return AbsencePersonnelEntity.findById(id);
    }

    @Override
    public List<AbsencePersonnelEntity> findByUtilisateurId(UUID utilisateurId) {
        return AbsencePersonnelEntity.list("utilisateurId", utilisateurId);
    }

    @Override
    public AbsencePersonnelEntity persist(AbsencePersonnelEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(AbsencePersonnelEntity entity) {
        entity.delete();
    }
}
