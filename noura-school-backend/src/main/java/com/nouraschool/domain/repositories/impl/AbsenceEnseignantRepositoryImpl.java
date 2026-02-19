package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.AbsenceEnseignantEntity;
import com.nouraschool.domain.repositories.AbsenceEnseignantRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AbsenceEnseignantRepositoryImpl implements AbsenceEnseignantRepository {

    @Override
    public List<AbsenceEnseignantEntity> findAll() {
        return AbsenceEnseignantEntity.listAll();
    }

    @Override
    public List<AbsenceEnseignantEntity> findByEnseignantId(UUID enseignantId) {
        return AbsenceEnseignantEntity.list("enseignantEntity.id", enseignantId);
    }

    @Override
    public AbsenceEnseignantEntity findById(UUID id) {
        return AbsenceEnseignantEntity.findById(id);
    }

    @Override
    public AbsenceEnseignantEntity persist(AbsenceEnseignantEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(AbsenceEnseignantEntity entity) {
        entity.delete();
    }
}
