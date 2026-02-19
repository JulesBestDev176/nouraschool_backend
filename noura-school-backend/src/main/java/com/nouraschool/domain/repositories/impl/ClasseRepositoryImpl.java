package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.ClasseEntity;
import com.nouraschool.domain.repositories.ClasseRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClasseRepositoryImpl implements ClasseRepository {

    @Override
    public List<ClasseEntity> findAll() {
        return ClasseEntity.listAll();
    }

    @Override
    public ClasseEntity findById(UUID id) {
        return ClasseEntity.findById(id);
    }

    @Override
    public ClasseEntity persist(ClasseEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(ClasseEntity entity) {
        entity.delete();
    }
}
