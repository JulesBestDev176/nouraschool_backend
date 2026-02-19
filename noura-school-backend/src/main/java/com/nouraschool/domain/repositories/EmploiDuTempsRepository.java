package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.EmploiDuTempsEntity;

import java.util.List;
import java.util.UUID;

public interface EmploiDuTempsRepository {

    List<EmploiDuTempsEntity> findAll();

    List<EmploiDuTempsEntity> findByClasseId(UUID classeId);

    List<EmploiDuTempsEntity> findByEnseignantId(UUID enseignantId);

    EmploiDuTempsEntity findById(UUID id);

    EmploiDuTempsEntity persist(EmploiDuTempsEntity entity);

    void delete(EmploiDuTempsEntity entity);
}
