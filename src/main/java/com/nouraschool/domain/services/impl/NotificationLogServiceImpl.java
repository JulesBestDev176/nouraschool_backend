package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.entities.NotificationLogEntity;
import com.nouraschool.domain.services.NotificationLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class NotificationLogServiceImpl implements NotificationLogService {

    @Override
    @Transactional
    public UUID log(UUID tenantId, String canal, String destinataire, String sujet, String contenu) {
        var entity = new NotificationLogEntity();
        entity.tenantId = tenantId;
        entity.canal = canal != null ? canal : CANAL_IN_APP;
        entity.destinataire = destinataire;
        entity.sujet = sujet;
        entity.contenu = contenu;
        entity.statut = STATUT_EN_ATTENTE;
        entity.persist();
        return entity.id;
    }

    @Override
    @Transactional
    public void markSent(UUID logId) {
        NotificationLogEntity entity = NotificationLogEntity.findById(logId);
        if (entity != null) {
            entity.statut = STATUT_ENVOYE;
            entity.envoyeLe = Instant.now();
        }
    }

    @Override
    @Transactional
    public void markFailed(UUID logId, String erreur) {
        NotificationLogEntity entity = NotificationLogEntity.findById(logId);
        if (entity != null) {
            entity.statut = STATUT_ECHEC;
            entity.erreur = erreur != null ? (erreur.length() > 10000 ? erreur.substring(0, 10000) : erreur) : null;
        }
    }
}
