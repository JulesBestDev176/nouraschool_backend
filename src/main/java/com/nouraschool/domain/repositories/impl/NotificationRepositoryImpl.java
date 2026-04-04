package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.NotificationEntity;
import com.nouraschool.domain.repositories.NotificationRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NotificationRepositoryImpl implements NotificationRepository {

    @Override
    public List<NotificationEntity> findAll() {
        return NotificationEntity.listAll();
    }

    @Override
    public List<NotificationEntity> findByUserId(UUID userId) {
        return NotificationEntity.list("userId", userId);
    }

    @Override
    public NotificationEntity findById(UUID id) {
        return NotificationEntity.findById(id);
    }

    @Override
    public NotificationEntity persist(NotificationEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(NotificationEntity entity) {
        entity.delete();
    }
}
