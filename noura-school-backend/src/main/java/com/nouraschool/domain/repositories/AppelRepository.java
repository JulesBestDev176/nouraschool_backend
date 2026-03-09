package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.AppelEntity;

import java.util.List;
import java.util.UUID;

public interface AppelRepository {

    List<AppelEntity> findByCoursId(UUID coursId);

    AppelEntity findById(UUID id);

    AppelEntity persist(AppelEntity entity);

    void delete(AppelEntity entity);
}
