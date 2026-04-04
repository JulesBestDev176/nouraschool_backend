package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.dtos.PageRequest;
import com.nouraschool.domain.entities.EleveEntity;
import com.nouraschool.domain.repositories.EleveRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EleveRepositoryImpl implements EleveRepository {

    private static final List<String> ALLOWED_SORT = List.of("id", "matricule", "firstName", "lastName", "dateNaissance", "dateInscription");

    @Override
    public List<EleveEntity> findAll() {
        return EleveEntity.listAll();
    }

    @Override
    public List<EleveEntity> findByClasseId(UUID classeId) {
        return EleveEntity.list("classe.id", classeId);
    }

    @Override
    public List<EleveEntity> findAll(PageRequest pageRequest) {
        var page = Page.of(pageRequest.getPage(), pageRequest.getSize());
        var sortBy = pageRequest.getSortBy() != null && ALLOWED_SORT.contains(pageRequest.getSortBy())
                ? pageRequest.getSortBy() : "id";
        var order = pageRequest.isAscending() ? " asc" : " desc";
        return EleveEntity.find("ORDER BY " + sortBy + order).page(page).list();
    }

    @Override
    public long count() {
        return EleveEntity.count();
    }

    @Override
    public EleveEntity findById(UUID id) {
        return EleveEntity.findById(id);
    }

    @Override
    public EleveEntity persist(EleveEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(EleveEntity entity) {
        entity.delete();
    }
}
