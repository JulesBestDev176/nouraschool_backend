package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.PaiementEntity;
import com.nouraschool.domain.repositories.PaiementRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PaiementRepositoryImpl implements PaiementRepository {

    @Override
    public List<PaiementEntity> findAll() {
        return PaiementEntity.listAll();
    }

    @Override
    public List<PaiementEntity> findByEleveId(UUID eleveId) {
        return PaiementEntity.list("eleve.id", eleveId);
    }

    @Override
    public List<PaiementEntity> findByParentId(UUID parentId) {
        return PaiementEntity.list("parent.id", parentId);
    }

    @Override
    public List<PaiementEntity> findBetweenDates(LocalDateTime debut, LocalDateTime fin) {
        return PaiementEntity.list("createdAt between ?1 and ?2", debut, fin);
    }

    @Override
    public PaiementEntity findById(UUID id) {
        return PaiementEntity.findById(id);
    }

    @Override
    public PaiementEntity persist(PaiementEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(PaiementEntity entity) {
        entity.delete();
    }
}
