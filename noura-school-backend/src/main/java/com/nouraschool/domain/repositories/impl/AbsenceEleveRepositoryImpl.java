package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.AbsenceEleveEntity;
import com.nouraschool.domain.repositories.AbsenceEleveRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AbsenceEleveRepositoryImpl implements AbsenceEleveRepository {

    @Override
    public List<AbsenceEleveEntity> findAll() {
        return AbsenceEleveEntity.listAll();
    }

    @Override
    public List<AbsenceEleveEntity> findByEleveId(UUID eleveId) {
        return AbsenceEleveEntity.list("eleve.id", eleveId);
    }

    @Override
    public AbsenceEleveEntity findById(UUID id) {
        return AbsenceEleveEntity.findById(id);
    }

    @Override
    public AbsenceEleveEntity persist(AbsenceEleveEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(AbsenceEleveEntity entity) {
        entity.delete();
    }
}
