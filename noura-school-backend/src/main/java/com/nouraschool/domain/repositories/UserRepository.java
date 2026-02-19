package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    List<UserEntity> findAll();

    UserEntity findById(UUID id);

    UserEntity findByUsernameOrEmail(String identifier);

    UserEntity findByUsername(String username);

    UserEntity persist(UserEntity user);

    void delete(UserEntity user);
}
