package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.NoteEntity;
import com.nouraschool.domain.repositories.NoteRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NoteRepositoryImpl implements NoteRepository {

    @Override
    public List<NoteEntity> findAll() {
        return NoteEntity.listAll();
    }

    @Override
    public List<NoteEntity> findByEleveId(UUID eleveId) {
        return NoteEntity.list("eleve.id", eleveId);
    }

    @Override
    public List<NoteEntity> findByEleveIdAndTrimestreAndAnnee(UUID eleveId, String trimestre, String anneeScolaire) {
        return NoteEntity.list("eleve.id = ?1 and trimestre = ?2 and anneeScolaire = ?3",
                eleveId, trimestre, anneeScolaire);
    }

    @Override
    public NoteEntity findById(UUID id) {
        return NoteEntity.findById(id);
    }

    @Override
    public NoteEntity persist(NoteEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(NoteEntity entity) {
        entity.delete();
    }
}
