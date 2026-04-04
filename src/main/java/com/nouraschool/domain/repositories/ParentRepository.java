package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.ParentEntity;

import java.util.List;
import java.util.UUID;

public interface ParentRepository {

    List<ParentEntity> findAll();

    ParentEntity findById(UUID id);

    ParentEntity persist(ParentEntity entity);

    void delete(ParentEntity entity);
}
