package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.AbsencePersonnelEntity;

import java.util.List;
import java.util.UUID;

public interface AbsencePersonnelRepository {

    List<AbsencePersonnelEntity> findByTenantId(UUID tenantId);

    AbsencePersonnelEntity findById(UUID id);

    List<AbsencePersonnelEntity> findByUtilisateurId(UUID utilisateurId);

    AbsencePersonnelEntity persist(AbsencePersonnelEntity entity);

    void delete(AbsencePersonnelEntity entity);
}
