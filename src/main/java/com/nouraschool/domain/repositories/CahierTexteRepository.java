package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.CahierTexteEntity;

import java.util.List;
import java.util.UUID;

public interface CahierTexteRepository {

    List<CahierTexteEntity> findByCoursId(UUID coursId);

    CahierTexteEntity findById(UUID id);

    CahierTexteEntity persist(CahierTexteEntity entity);

    void delete(CahierTexteEntity entity);
}
