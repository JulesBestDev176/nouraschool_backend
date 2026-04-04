package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.NoteEntity;

import java.util.List;
import java.util.UUID;

public interface NoteRepository {

    List<NoteEntity> findAll();

    List<NoteEntity> findByEleveId(UUID eleveId);

    List<NoteEntity> findByEleveIdAndTrimestreAndAnnee(UUID eleveId, String trimestre, String anneeScolaire);

    NoteEntity findById(UUID id);

    NoteEntity persist(NoteEntity entity);

    void delete(NoteEntity entity);
}
