package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.AppelLigneEntity;
import com.nouraschool.domain.repositories.AppelLigneRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AppelLigneRepositoryImpl implements AppelLigneRepository {
    @Override
    public List<AppelLigneEntity> findByAppelId(UUID appelId) {
        return AppelLigneEntity.list("appelId", appelId);
    }
    @Override
    public AppelLigneEntity findById(UUID id) {
        return AppelLigneEntity.findById(id);
    }
    @Override
    public AppelLigneEntity persist(AppelLigneEntity entity) {
        entity.persist();
        return entity;
    }
    @Override
    public void delete(AppelLigneEntity entity) {
        entity.delete();
    }
}
