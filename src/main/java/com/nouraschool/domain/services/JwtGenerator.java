package com.nouraschool.domain.services;

import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.entities.PlateformeUtilisateurEntity;

import java.util.Set;

public interface JwtGenerator {

    String generateAccessToken(UserEntity user, Set<String> roles, long lifespan);

    String generatePlatformAccessToken(PlateformeUtilisateurEntity user, Set<String> roles, long lifespan);
}
