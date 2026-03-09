package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.CoursEntity;

import java.util.List;
import java.util.UUID;

public interface CoursRepository {

    List<CoursEntity> findByTenantId(UUID tenantId);

    List<CoursEntity> findByClasseId(UUID classeId);

    List<CoursEntity> findByProfesseurId(UUID professeurId);

    CoursEntity findById(UUID id);

    CoursEntity persist(CoursEntity entity);

    void delete(CoursEntity entity);
}
