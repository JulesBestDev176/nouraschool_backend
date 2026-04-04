package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.SurveillantCycleEntity;
import com.nouraschool.domain.repositories.SurveillantCycleRepository;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ApplicationScoped
public class SurveillantCycleRepositoryImpl implements SurveillantCycleRepository {

    @Override
    public List<SurveillantCycleEntity> findBySurveillantId(UUID surveillantId) {
        return SurveillantCycleEntity.list("surveillantId", surveillantId);
    }

    @Override
    public boolean exists(UUID surveillantId, UUID cycleId) {
        Optional<SurveillantCycleEntity> found = SurveillantCycleEntity.find(
                "surveillantId = ?1 and cycleId = ?2", surveillantId, cycleId).firstResultOptional();
        return found.isPresent();
    }

    @Override
    public SurveillantCycleEntity persist(SurveillantCycleEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(UUID surveillantId, UUID cycleId) {
        SurveillantCycleEntity.delete("surveillantId = ?1 and cycleId = ?2", surveillantId, cycleId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> findCycleCodesBySurveillantId(UUID surveillantId) {
        return Panache.getEntityManager()
                .createQuery("SELECT c.code FROM SurveillantCycleEntity sc JOIN sc.cycle c WHERE sc.surveillantId = :sid")
                .setParameter("sid", surveillantId)
                .getResultList();
    }
}
