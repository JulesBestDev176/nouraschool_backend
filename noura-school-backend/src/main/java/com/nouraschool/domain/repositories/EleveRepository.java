package com.nouraschool.domain.repositories;

import com.nouraschool.domain.dtos.PageRequest;
import com.nouraschool.domain.entities.EleveEntity;

import java.util.List;
import java.util.UUID;

public interface EleveRepository {

    List<EleveEntity> findAll();

    List<EleveEntity> findAll(PageRequest pageRequest);

    long count();

    EleveEntity findById(UUID id);

    EleveEntity persist(EleveEntity entity);

    void delete(EleveEntity entity);
}
