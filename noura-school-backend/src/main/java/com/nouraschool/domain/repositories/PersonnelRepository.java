package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.PersonnelEntity;

import java.util.List;
import java.util.UUID;

public interface PersonnelRepository {

    List<PersonnelEntity> findByTenantId(UUID tenantId);

    PersonnelEntity findById(UUID id);

    List<PersonnelEntity> findByUtilisateurId(UUID utilisateurId);

    PersonnelEntity persist(PersonnelEntity entity);

    void delete(PersonnelEntity entity);
}
