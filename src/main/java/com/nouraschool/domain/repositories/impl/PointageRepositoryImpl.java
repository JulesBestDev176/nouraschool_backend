package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.PointageEntity;
import com.nouraschool.domain.repositories.PointageRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PointageRepositoryImpl implements PointageRepository {

    @Override
    public List<PointageEntity> findByTenantId(UUID tenantId) {
        return PointageEntity.list("tenantId", tenantId);
    }

    @Override
    public List<PointageEntity> findByUtilisateurId(UUID utilisateurId) {
        return PointageEntity.list("utilisateurId", utilisateurId);
    }

    @Override
    public List<PointageEntity> findByDateRange(UUID tenantId, Instant debut, Instant fin) {
        return PointageEntity.find("tenantId = ?1 and dateHeure >= ?2 and dateHeure <= ?3", tenantId, debut, fin).list();
    }

    @Override
    public PointageEntity findById(UUID id) {
        return PointageEntity.findById(id);
    }

    @Override
    public PointageEntity persist(PointageEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(PointageEntity entity) {
        entity.delete();
    }
}
