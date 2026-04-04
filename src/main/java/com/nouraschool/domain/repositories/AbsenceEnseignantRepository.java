package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.AbsenceEnseignantEntity;

import java.util.List;
import java.util.UUID;

public interface AbsenceEnseignantRepository {

    List<AbsenceEnseignantEntity> findAll();

    List<AbsenceEnseignantEntity> findByEnseignantId(UUID enseignantId);

    AbsenceEnseignantEntity findById(UUID id);

    AbsenceEnseignantEntity persist(AbsenceEnseignantEntity entity);

    void delete(AbsenceEnseignantEntity entity);
}
