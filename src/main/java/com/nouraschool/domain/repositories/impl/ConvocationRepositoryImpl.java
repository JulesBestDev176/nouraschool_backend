package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.ConvocationEntity;
import com.nouraschool.domain.repositories.ConvocationRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ConvocationRepositoryImpl implements ConvocationRepository {

    @Override
    public List<ConvocationEntity> findByTenantId(UUID tenantId) {
        return ConvocationEntity.list("tenantId", tenantId);
    }

    @Override
    public ConvocationEntity findById(UUID id) {
        return ConvocationEntity.findById(id);
    }

    @Override
    public ConvocationEntity persist(ConvocationEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(ConvocationEntity entity) {
        if (entity != null) {
            entity.delete();
        }
    }
}
