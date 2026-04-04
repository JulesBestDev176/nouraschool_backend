package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.ReclamationEntity;

import java.util.List;
import java.util.UUID;

public interface ReclamationRepository {

    List<ReclamationEntity> findAll();

    List<ReclamationEntity> findByEleveId(UUID eleveId);

    List<ReclamationEntity> findByNoteMatiereIdIn(List<UUID> matiereIds);

    ReclamationEntity findById(UUID id);

    ReclamationEntity persist(ReclamationEntity entity);

    void delete(ReclamationEntity entity);
}
