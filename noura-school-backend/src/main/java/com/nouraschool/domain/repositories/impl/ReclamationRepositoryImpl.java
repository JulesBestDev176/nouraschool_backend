package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.ReclamationEntity;
import com.nouraschool.domain.repositories.ReclamationRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ReclamationRepositoryImpl implements ReclamationRepository {

    @Override
    public List<ReclamationEntity> findAll() {
        return ReclamationEntity.listAll();
    }

    @Override
    public List<ReclamationEntity> findByEleveId(UUID eleveId) {
        return ReclamationEntity.list("eleve.id", eleveId);
    }

    @Override
    public List<ReclamationEntity> findByNoteMatiereIdIn(List<UUID> matiereIds) {
        if (matiereIds == null || matiereIds.isEmpty()) return List.of();
        return ReclamationEntity.list("note.matiere.id in ?1", matiereIds);
    }

    @Override
    public ReclamationEntity findById(UUID id) {
        return ReclamationEntity.findById(id);
    }

    @Override
    public ReclamationEntity persist(ReclamationEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(ReclamationEntity entity) {
        entity.delete();
    }
}
