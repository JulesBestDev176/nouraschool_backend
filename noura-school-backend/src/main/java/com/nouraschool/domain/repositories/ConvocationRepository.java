package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.ConvocationEntity;

import java.util.List;
import java.util.UUID;

public interface ConvocationRepository {

    List<ConvocationEntity> findByTenantId(UUID tenantId);

    ConvocationEntity findById(UUID id);

    ConvocationEntity persist(ConvocationEntity entity);

    void delete(ConvocationEntity entity);
}
