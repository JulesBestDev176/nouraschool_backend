package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.MatiereClasseEntity;

import java.util.List;
import java.util.UUID;

public interface MatiereClasseRepository {

    List<MatiereClasseEntity> findAll();

    List<MatiereClasseEntity> findByEnseignantId(UUID enseignantId);

    MatiereClasseEntity findById(UUID id);

    MatiereClasseEntity persist(MatiereClasseEntity entity);

    void delete(MatiereClasseEntity entity);
}
