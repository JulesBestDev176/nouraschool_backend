package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.NotificationEntity;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository {

    List<NotificationEntity> findAll();

    List<NotificationEntity> findByUserId(UUID userId);

    NotificationEntity findById(UUID id);

    NotificationEntity persist(NotificationEntity entity);

    void delete(NotificationEntity entity);
}
