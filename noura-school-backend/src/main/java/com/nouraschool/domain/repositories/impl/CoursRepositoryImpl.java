package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.CoursEntity;
import com.nouraschool.domain.repositories.CoursRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CoursRepositoryImpl implements CoursRepository {

    @Override
    public List<CoursEntity> findByTenantId(UUID tenantId) {
        return CoursEntity.list("tenantId", tenantId);
    }

    @Override
    public List<CoursEntity> findByClasseId(UUID classeId) {
        return CoursEntity.list("classeId", classeId);
    }

    @Override
    public List<CoursEntity> findByProfesseurId(UUID professeurId) {
        return CoursEntity.list("professeurId", professeurId);
    }

    @Override
    public CoursEntity findById(UUID id) {
        return CoursEntity.findById(id);
    }

    @Override
    public CoursEntity persist(CoursEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(CoursEntity entity) {
        entity.delete();
    }
}
