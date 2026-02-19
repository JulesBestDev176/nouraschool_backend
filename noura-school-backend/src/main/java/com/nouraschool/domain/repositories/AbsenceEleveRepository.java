package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.AbsenceEleveEntity;

import java.util.List;
import java.util.UUID;

public interface AbsenceEleveRepository {

    List<AbsenceEleveEntity> findAll();

    List<AbsenceEleveEntity> findByEleveId(UUID eleveId);

    AbsenceEleveEntity findById(UUID id);

    AbsenceEleveEntity persist(AbsenceEleveEntity entity);

    void delete(AbsenceEleveEntity entity);
}
