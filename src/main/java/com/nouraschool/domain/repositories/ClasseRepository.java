package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.ClasseEntity;

import java.util.List;
import java.util.UUID;

public interface ClasseRepository {

    List<ClasseEntity> findAll();

    ClasseEntity findById(UUID id);

    ClasseEntity persist(ClasseEntity entity);

    void delete(ClasseEntity entity);
}
