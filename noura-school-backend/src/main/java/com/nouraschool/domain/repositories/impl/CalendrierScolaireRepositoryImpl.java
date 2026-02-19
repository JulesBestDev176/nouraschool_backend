package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.CalendrierScolaireEntity;
import com.nouraschool.domain.repositories.CalendrierScolaireRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CalendrierScolaireRepositoryImpl implements CalendrierScolaireRepository {

    @Override
    public List<CalendrierScolaireEntity> findAll() {
        return CalendrierScolaireEntity.listAll();
    }

    @Override
    public CalendrierScolaireEntity findById(UUID id) {
        return CalendrierScolaireEntity.findById(id);
    }

    @Override
    public CalendrierScolaireEntity persist(CalendrierScolaireEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(CalendrierScolaireEntity entity) {
        entity.delete();
    }
}
