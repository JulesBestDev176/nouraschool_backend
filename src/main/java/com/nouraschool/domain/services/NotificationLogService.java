package com.nouraschool.domain.services;

import java.util.UUID;

/**
 * Enregistrement des notifications dans notification_log avant envoi (task.md section 15).
 * Toute notification doit être loggée ici ; les envois (WhatsApp, email, in-app) mettent à jour le statut.
 */
public interface NotificationLogService {

    /** Canaux supportés (task.md). */
    String CANAL_WHATSAPP = "WHATSAPP";
    String CANAL_EMAIL = "EMAIL";
    String CANAL_IN_APP = "IN_APP";

    /** Statuts. */
    String STATUT_EN_ATTENTE = "EN_ATTENTE";
    String STATUT_ENVOYE = "ENVOYE";
    String STATUT_ECHEC = "ECHEC";

    /**
     * Enregistre une notification en base (statut EN_ATTENTE) avant envoi.
     *
     * @param tenantId    nullable
     * @param canal       WHATSAPP | EMAIL | IN_APP
     * @param destinataire email ou téléphone
     * @param sujet       nullable
     * @param contenu     corps du message
     * @return id du log créé (pour mise à jour du statut après envoi)
     */
    UUID log(UUID tenantId, String canal, String destinataire, String sujet, String contenu);

    /**
     * Marque le log comme envoyé.
     */
    void markSent(UUID logId);

    /**
     * Marque le log en échec avec le message d'erreur.
     */
    void markFailed(UUID logId, String erreur);
}
