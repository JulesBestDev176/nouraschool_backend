package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.AppelEntity;
import com.nouraschool.domain.repositories.AppelRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AppelRepositoryImpl implements AppelRepository {

    @Override
    public List<AppelEntity> findByCoursId(UUID coursId) {
        return AppelEntity.list("coursId", coursId);
    }

    @Override
    public AppelEntity findById(UUID id) {
        return AppelEntity.findById(id);
    }

    @Override
    public AppelEntity persist(AppelEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(AppelEntity entity) {
        entity.delete();
    }
}
