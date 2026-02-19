package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.MatiereClasseEntity;
import com.nouraschool.domain.repositories.MatiereClasseRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MatiereClasseRepositoryImpl implements MatiereClasseRepository {

    @Override
    public List<MatiereClasseEntity> findAll() {
        return MatiereClasseEntity.listAll();
    }

    @Override
    public List<MatiereClasseEntity> findByEnseignantId(UUID enseignantId) {
        return MatiereClasseEntity.list("enseignantEntity.id", enseignantId);
    }

    @Override
    public MatiereClasseEntity findById(UUID id) {
        return MatiereClasseEntity.findById(id);
    }

    @Override
    public MatiereClasseEntity persist(MatiereClasseEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(MatiereClasseEntity entity) {
        entity.delete();
    }
}
