package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.repositories.UserRepository;
import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    @Inject
    JPAStreamer jpaStreamer;

    @Override
    public List<UserEntity> findAll() {
        return UserEntity.listAll();
    }

    @Override
    public UserEntity findById(UUID id) {
        return UserEntity.findById(id);
    }

    @Override
    public UserEntity findByUsernameOrEmail(String identifier) {
        return jpaStreamer.stream(UserEntity.class)
                .filter(u -> identifier.equals(u.username) || identifier.equals(u.email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return jpaStreamer.stream(UserEntity.class)
                .filter(u -> username.equals(u.username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserEntity persist(UserEntity user) {
        user.persist();
        return user;
    }

    @Override
    public void delete(UserEntity user) {
        user.delete();
    }
}
