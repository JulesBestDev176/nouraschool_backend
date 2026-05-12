package com.nouraschool.domain.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nouraschool.domain.services.EmailService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class EmailServiceImpl implements EmailService {

    private static final Logger LOG = Logger.getLogger(EmailServiceImpl.class);
    private static final URI RESEND_EMAILS_URI = URI.create("https://api.resend.com/emails");

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "app.mail.resend.api-key", defaultValue = "")
    String resendApiKey;

    @ConfigProperty(name = "app.mail.from", defaultValue = "NouraSchool <onboarding@resend.dev>")
    String from;

    @ConfigProperty(name = "app.mail.reply-to", defaultValue = "")
    String replyTo;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void send(String to, String subject, String body) {
        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new IllegalStateException("RESEND_API_KEY is not configured");
        }

        try {
            String payload = objectMapper.writeValueAsString(buildResendPayload(to, subject, body));
            HttpRequest request = HttpRequest.newBuilder(RESEND_EMAILS_URI)
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOG.warnv("Resend email failed status={0} to={1} body={2}", response.statusCode(), maskEmail(to), response.body());
                throw new IllegalStateException("Resend email failed with status " + response.statusCode());
            }
            LOG.infov("Resend email sent to={0} subject={1}", maskEmail(to), subject);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize Resend email payload", e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to call Resend API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Resend email call interrupted", e);
        }
    }

    private Map<String, Object> buildResendPayload(String to, String subject, String body) {
        Map<String, Object> payload = new java.util.LinkedHashMap<>();
        payload.put("from", from);
        payload.put("to", List.of(to));
        payload.put("subject", subject);
        payload.put("text", body);
        payload.put("html", toHtml(body));
        if (replyTo != null && !replyTo.isBlank()) {
            payload.put("reply_to", replyTo);
        }
        return payload;
    }

    private String toHtml(String body) {
        String safeBody = body == null ? "" : body
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        return "<pre style=\"font-family:Arial,sans-serif;white-space:pre-wrap;line-height:1.5\">" + safeBody + "</pre>";
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "blank";
        }
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    @Override
    public void sendCompteCree(String to, String nom, String prenom, String email, String motDePasseTemporaire) {
        String subject = "Votre compte Noura School a ete cree";
        String body = String.format(
                "Bonjour %s %s,%n%n"
                + "Votre compte a ete cree. Voici vos identifiants temporaires :%n%n"
                + "Email : %s%n"
                + "Mot de passe temporaire : %s%n%n"
                + "Veuillez modifier ce mot de passe lors de votre premiere connexion.%n%n"
                + "Cordialement,%nL'equipe Noura School",
                prenom, nom, email, motDePasseTemporaire);
        send(to, subject, body);
    }

    @Override
    public void sendResetMdp(String to, String resetToken, String expirationMinutes) {
        String subject = "Reinitialisation de votre mot de passe";
        String body = String.format(
                "Bonjour,%n%n"
                + "Vous avez demande une reinitialisation de mot de passe.%n%n"
                + "Utilisez ce lien ou token : %s%n%n"
                + "Ce lien expire dans %s minutes.%n%n"
                + "Si vous n'avez pas fait cette demande, ignorez cet email.%n%n"
                + "Cordialement,%nL'equipe Noura School",
                resetToken, expirationMinutes);
        send(to, subject, body);
    }

    @Override
    public void sendBulletinDisponible(String to, String eleveNom, String trimestre) {
        String subject = "Bulletin scolaire disponible";
        String body = String.format(
                "Bonjour,%n%n"
                + "Le bulletin de %s pour le trimestre %s est maintenant disponible.%n%n"
                + "Connectez-vous a votre espace pour le consulter.%n%n"
                + "Cordialement,%nL'equipe Noura School",
                eleveNom, trimestre);
        send(to, subject, body);
    }
}
