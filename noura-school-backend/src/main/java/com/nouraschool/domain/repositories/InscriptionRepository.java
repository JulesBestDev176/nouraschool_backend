package com.nouraschool.domain.repositories;

import com.nouraschool.domain.entities.InscriptionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InscriptionRepository {

    List<InscriptionEntity> findByTenantId(UUID tenantId);

    InscriptionEntity findById(UUID id);

    Optional<InscriptionEntity> findByEleveAndAnneeAcademique(UUID eleveId, UUID anneeAcademiqueId);

    InscriptionEntity findByNumeroInscription(String numeroInscription);

    long countActifsByClasseAndAnnee(UUID classeId, UUID anneeAcademiqueId);

    InscriptionEntity persist(InscriptionEntity entity);

    void delete(InscriptionEntity entity);
}
