package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.EnseignantEntity;

import java.util.List;
import java.util.UUID;

public interface EnseignantRepository {

    List<EnseignantEntity> findAll();

    EnseignantEntity findById(UUID id);

    EnseignantEntity persist(EnseignantEntity entity);

    void delete(EnseignantEntity entity);
}
