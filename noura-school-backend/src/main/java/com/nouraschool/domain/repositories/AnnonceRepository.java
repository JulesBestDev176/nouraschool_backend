package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.AnnonceEntity;

import java.util.List;
import java.util.UUID;

public interface AnnonceRepository {

    List<AnnonceEntity> findByTenantId(UUID tenantId);

    AnnonceEntity findById(UUID id);

    AnnonceEntity persist(AnnonceEntity entity);

    void delete(AnnonceEntity entity);
}
