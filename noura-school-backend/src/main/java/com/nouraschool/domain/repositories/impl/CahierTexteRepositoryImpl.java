package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.CahierTexteEntity;
import com.nouraschool.domain.repositories.CahierTexteRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CahierTexteRepositoryImpl implements CahierTexteRepository {

    @Override
    public List<CahierTexteEntity> findByCoursId(UUID coursId) {
        return CahierTexteEntity.list("coursId", coursId);
    }

    @Override
    public CahierTexteEntity findById(UUID id) {
        return CahierTexteEntity.findById(id);
    }

    @Override
    public CahierTexteEntity persist(CahierTexteEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(CahierTexteEntity entity) {
        entity.delete();
    }
}
