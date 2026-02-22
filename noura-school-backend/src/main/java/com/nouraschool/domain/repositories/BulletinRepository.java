package com.nouraschool.domain.repositories;

import com.nouraschool.domain.dtos.PageRequest;
import com.nouraschool.domain.entities.BulletinEntity;

import java.util.List;
import java.util.UUID;

public interface BulletinRepository {

    List<BulletinEntity> findAll();

    List<BulletinEntity> findAll(PageRequest pageRequest);

    List<BulletinEntity> findByEleveId(UUID eleveId);

    List<BulletinEntity> findByClasseIdAndAnneeAndTrimestre(UUID classeId, String anneeScolaire, String trimestre);

    List<BulletinEntity> findAllByAnneeAndTrimestre(String anneeScolaire, String trimestre);

    long count();

    BulletinEntity findById(UUID id);

    BulletinEntity persist(BulletinEntity entity);

    void delete(BulletinEntity entity);
}
