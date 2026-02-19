package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.ParentEntity;
import com.nouraschool.domain.repositories.ParentRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ParentRepositoryImpl implements ParentRepository {

    @Override
    public List<ParentEntity> findAll() {
        return ParentEntity.listAll();
    }

    @Override
    public ParentEntity findById(UUID id) {
        return ParentEntity.findById(id);
    }

    @Override
    public ParentEntity persist(ParentEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(ParentEntity entity) {
        entity.delete();
    }
}
