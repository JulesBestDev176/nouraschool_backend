package com.nouraschool.domain.services;

import com.nouraschool.domain.entities.UserEntity;

import java.util.Set;

public interface JwtGenerator {

    String generateAccessToken(UserEntity user, Set<String> roles, long lifespan);
}
