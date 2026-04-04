package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.PlateformeUtilisateurEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlateformeUtilisateurRepository {

    List<PlateformeUtilisateurEntity> findAll();

    Optional<PlateformeUtilisateurEntity> findById(UUID id);

    Optional<PlateformeUtilisateurEntity> findByEmail(String email);

    PlateformeUtilisateurEntity persist(PlateformeUtilisateurEntity entity);

    void delete(PlateformeUtilisateurEntity entity);
}
