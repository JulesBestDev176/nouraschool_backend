package com.nouraschool.domain.services;

/**
 * Service d'envoi d'emails (SMTP).
 * Templates : compte créé, reset mdp, bulletin — task.md section 15.
 */
public interface EmailService {

    /**
     * Envoie un email.
     *
     * @param to      destinataire
     * @param subject sujet
     * @param body    corps HTML ou texte
     */
    void send(String to, String subject, String body);

    /**
     * Envoie les identifiants temporaires lors de la création de compte.
     */
    void sendCompteCree(String to, String nom, String prenom, String email, String motDePasseTemporaire);

    /**
     * Envoie le lien de réinitialisation de mot de passe.
     */
    void sendResetMdp(String to, String resetToken, String expirationMinutes);

    /**
     * Envoie une notification bulletin disponible.
     */
    void sendBulletinDisponible(String to, String eleveNom, String trimestre);
}
