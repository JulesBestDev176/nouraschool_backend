package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.PlateformeUtilisateurEntity;
import com.nouraschool.domain.repositories.PlateformeUtilisateurRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PlateformeUtilisateurRepositoryImpl implements PlateformeUtilisateurRepository {

    @Override
    public List<PlateformeUtilisateurEntity> findAll() {
        return PlateformeUtilisateurEntity.listAll();
    }

    @Override
    public Optional<PlateformeUtilisateurEntity> findById(UUID id) {
        return Optional.ofNullable(PlateformeUtilisateurEntity.findById(id));
    }

    @Override
    public Optional<PlateformeUtilisateurEntity> findByEmail(String email) {
        return PlateformeUtilisateurEntity.find("email", email).firstResultOptional();
    }

    @Override
    public PlateformeUtilisateurEntity persist(PlateformeUtilisateurEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(PlateformeUtilisateurEntity entity) {
        entity.delete();
    }
}
