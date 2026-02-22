package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.dtos.PageRequest;
import com.nouraschool.domain.entities.BulletinEntity;
import com.nouraschool.domain.repositories.BulletinRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BulletinRepositoryImpl implements BulletinRepository {

    @Override
    public List<BulletinEntity> findAll() {
        return BulletinEntity.listAll();
    }

    private static final List<String> ALLOWED_SORT_FIELDS = List.of("id", "trimestre", "anneeScolaire", "moyenne", "rang", "createdAt");

    @Override
    public List<BulletinEntity> findAll(PageRequest pageRequest) {
        var page = Page.of(pageRequest.getPage(), pageRequest.getSize());
        var sortBy = pageRequest.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(pageRequest.getSortBy())
                ? pageRequest.getSortBy() : "id";
        var order = pageRequest.isAscending() ? " asc" : " desc";
        return BulletinEntity.find("ORDER BY " + sortBy + order).page(page).list();
    }

    @Override
    public List<BulletinEntity> findByEleveId(UUID eleveId) {
        return BulletinEntity.list("eleve.id", eleveId);
    }

    @Override
    public List<BulletinEntity> findByClasseIdAndAnneeAndTrimestre(UUID classeId, String anneeScolaire, String trimestre) {
        return BulletinEntity.list(
                "eleve.classe.id = ?1 and anneeScolaire = ?2 and trimestre = ?3",
                classeId, anneeScolaire, trimestre
        );
    }

    @Override
    public List<BulletinEntity> findAllByAnneeAndTrimestre(String anneeScolaire, String trimestre) {
        return BulletinEntity.list("anneeScolaire = ?1 and trimestre = ?2", anneeScolaire, trimestre);
    }

    @Override
    public long count() {
        return BulletinEntity.count();
    }

    @Override
    public BulletinEntity findById(UUID id) {
        return BulletinEntity.findById(id);
    }

    @Override
    public BulletinEntity persist(BulletinEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(BulletinEntity entity) {
        entity.delete();
    }
}
