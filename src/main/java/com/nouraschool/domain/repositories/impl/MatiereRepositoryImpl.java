package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.MatiereEntity;
import com.nouraschool.domain.repositories.MatiereRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MatiereRepositoryImpl implements MatiereRepository {

    @Override
    public List<MatiereEntity> findAll() {
        return MatiereEntity.listAll();
    }

    @Override
    public MatiereEntity findById(UUID id) {
        return MatiereEntity.findById(id);
    }

    @Override
    public MatiereEntity persist(MatiereEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(MatiereEntity entity) {
        entity.delete();
    }
}
