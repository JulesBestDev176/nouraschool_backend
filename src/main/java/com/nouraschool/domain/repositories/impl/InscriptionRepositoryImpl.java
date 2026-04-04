package com.nouraschool.domain.repositories.impl;

import com.nouraschool.domain.entities.InscriptionEntity;
import com.nouraschool.domain.repositories.InscriptionRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class InscriptionRepositoryImpl implements InscriptionRepository {

    @Override
    public List<InscriptionEntity> findByTenantId(UUID tenantId) {
        return InscriptionEntity.list("tenantId", tenantId);
    }

    @Override
    public InscriptionEntity findById(UUID id) {
        return InscriptionEntity.findById(id);
    }

    @Override
    public Optional<InscriptionEntity> findByEleveAndAnneeAcademique(UUID eleveId, UUID anneeAcademiqueId) {
        return InscriptionEntity
                .find("eleve.id = ?1 and anneeAcademiqueId = ?2 and statut = ?3", eleveId, anneeAcademiqueId, "ACTIF")
                .firstResultOptional();
    }

    @Override
    public InscriptionEntity findByNumeroInscription(String numeroInscription) {
        return InscriptionEntity.find("numeroInscription", numeroInscription).firstResult();
    }

    @Override
    public long countActifsByClasseAndAnnee(UUID classeId, UUID anneeAcademiqueId) {
        return InscriptionEntity.count("classe.id = ?1 and anneeAcademiqueId = ?2 and statut = 'ACTIF'",
                classeId, anneeAcademiqueId);
    }

    @Override
    public InscriptionEntity persist(InscriptionEntity entity) {
        entity.persist();
        return entity;
    }

    @Override
    public void delete(InscriptionEntity entity) {
        entity.delete();
    }
}
