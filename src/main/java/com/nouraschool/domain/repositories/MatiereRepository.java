package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.MatiereEntity;

import java.util.List;
import java.util.UUID;

public interface MatiereRepository {

    List<MatiereEntity> findAll();

    MatiereEntity findById(UUID id);

    MatiereEntity persist(MatiereEntity entity);

    void delete(MatiereEntity entity);
}
