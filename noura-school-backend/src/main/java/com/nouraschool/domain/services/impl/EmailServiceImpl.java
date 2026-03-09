package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.services.EmailService;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailServiceImpl implements EmailService {

    @Inject
    Mailer mailer;

    @Override
    public void send(String to, String subject, String body) {
        mailer.send(Mail.withText(to, body).subject(subject));
    }

    @Override
    public void sendCompteCree(String to, String nom, String prenom, String email, String motDePasseTemporaire) {
        String subject = "Votre compte Noura School a Ã©tÃ© crÃ©Ã©";
        String body = String.format(
                "Bonjour %s %s,%n%n"
                + "Votre compte a Ã©tÃ© crÃ©Ã©. Voici vos identifiants temporaires :%n%n"
                + "Email : %s%n"
                + "Mot de passe temporaire : %s%n%n"
                + "Veuillez modifier ce mot de passe lors de votre premiÃ¨re connexion.%n%n"
                + "Cordialement,%nL'Ã©quipe Noura School",
                prenom, nom, email, motDePasseTemporaire);
        send(to, subject, body);
    }

    @Override
    public void sendResetMdp(String to, String resetToken, String expirationMinutes) {
        String subject = "RÃ©initialisation de votre mot de passe";
        String body = String.format(
                "Bonjour,%n%n"
                + "Vous avez demandÃ© une rÃ©initialisation de mot de passe.%n%n"
                + "Utilisez ce lien ou token : %s%n%n"
                + "Ce lien expire dans %s minutes.%n%n"
                + "Si vous n'avez pas fait cette demande, ignorez cet email.%n%n"
                + "Cordialement,%nL'Ã©quipe Noura School",
                resetToken, expirationMinutes);
        send(to, subject, body);
    }

    @Override
    public void sendBulletinDisponible(String to, String eleveNom, String trimestre) {
        String subject = "Bulletin scolaire disponible";
        String body = String.format(
                "Bonjour,%n%n"
                + "Le bulletin de %s pour le trimestre %s est maintenant disponible.%n%n"
                + "Connectez-vous Ã  votre espace pour le consulter.%n%n"
                + "Cordialement,%nL'Ã©quipe Noura School",
                eleveNom, trimestre);
        send(to, subject, body);
    }
}
