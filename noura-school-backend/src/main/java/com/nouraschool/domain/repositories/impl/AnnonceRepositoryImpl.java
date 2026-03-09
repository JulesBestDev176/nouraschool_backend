package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.AnnonceEntity;
import com.nouraschool.domain.repositories.AnnonceRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AnnonceRepositoryImpl implements AnnonceRepository {

    @Override
    public List<AnnonceEntity> findByTenantId(UUID tenantId) {
        return AnnonceEntity.list("tenantId", tenantId);
    }

    @Override
    public AnnonceEntity findById(UUID id) {
        return AnnonceEntity.findById(id);
    }

    @Override
    public AnnonceEntity persist(AnnonceEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(AnnonceEntity entity) {
        entity.delete();
    }
}
