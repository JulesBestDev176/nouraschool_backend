package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.AppelLigneEntity;
import java.util.List;
import java.util.UUID;

public interface AppelLigneRepository {
    List<AppelLigneEntity> findByAppelId(UUID appelId);
    AppelLigneEntity findById(UUID id);
    AppelLigneEntity persist(AppelLigneEntity entity);
    void delete(AppelLigneEntity entity);
}
