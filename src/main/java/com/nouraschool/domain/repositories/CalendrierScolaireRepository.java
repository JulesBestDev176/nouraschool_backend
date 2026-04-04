package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.CalendrierScolaireEntity;

import java.util.List;
import java.util.UUID;

public interface CalendrierScolaireRepository {

    List<CalendrierScolaireEntity> findAll();

    CalendrierScolaireEntity findById(UUID id);

    CalendrierScolaireEntity persist(CalendrierScolaireEntity entity);

    void delete(CalendrierScolaireEntity entity);
}
