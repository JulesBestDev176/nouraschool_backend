package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.EnseignantEntity;
import com.nouraschool.domain.repositories.EnseignantRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EnseignantRepositoryImpl implements EnseignantRepository {

    @Override
    public List<EnseignantEntity> findAll() {
        return EnseignantEntity.listAll();
    }

    @Override
    public EnseignantEntity findById(UUID id) {
        return EnseignantEntity.findById(id);
    }

    @Override
    public EnseignantEntity persist(EnseignantEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(EnseignantEntity entity) {
        entity.delete();
    }
}
