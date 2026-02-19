package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.EmploiDuTempsEntity;
import com.nouraschool.domain.repositories.EmploiDuTempsRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EmploiDuTempsRepositoryImpl implements EmploiDuTempsRepository {

    @Override
    public List<EmploiDuTempsEntity> findAll() {
        return EmploiDuTempsEntity.listAll();
    }

    @Override
    public List<EmploiDuTempsEntity> findByClasseId(UUID classeId) {
        return EmploiDuTempsEntity.list("classe.id", classeId);
    }

    @Override
    public List<EmploiDuTempsEntity> findByEnseignantId(UUID enseignantId) {
        return EmploiDuTempsEntity.list("enseignantEntity.id", enseignantId);
    }

    @Override
    public EmploiDuTempsEntity findById(UUID id) {
        return EmploiDuTempsEntity.findById(id);
    }

    @Override
    public EmploiDuTempsEntity persist(EmploiDuTempsEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(EmploiDuTempsEntity entity) {
        entity.delete();
    }
}
