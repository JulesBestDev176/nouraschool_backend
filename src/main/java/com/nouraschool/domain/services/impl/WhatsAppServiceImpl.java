package com.nouraschool.domain.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nouraschool.domain.services.WhatsAppService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Envoi WhatsApp via un gateway Node base sur whatsapp-web.js.
 * Le backend reste Java/Quarkus et appelle le gateway en HTTP interne.
 */
@ApplicationScoped
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final Logger LOG = Logger.getLogger(WhatsAppServiceImpl.class);

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "app.whatsapp.gateway.enabled", defaultValue = "false")
    boolean enabled;

    @ConfigProperty(name = "app.whatsapp.gateway.url", defaultValue = "http://localhost:3100")
    String gatewayUrl;

    @ConfigProperty(name = "app.whatsapp.gateway.api-key", defaultValue = "")
    String gatewayApiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public void sendOtp(String telephone, String code) {
        sendMessage(telephone, "Votre code NouraSchool est : " + code + ". Il expire dans 5 minutes.");
    }

    @Override
    public void sendAbsenceApprouvee(String telephone, String eleveNom, String date) {
        sendMessage(telephone, "Absence approuvee pour " + eleveNom + " le " + date + ".");
    }

    @Override
    public void sendConvocation(String telephone, String eleveNom, String date, String heure, String motif) {
        sendMessage(telephone, "Convocation pour " + eleveNom + " le " + date + " a " + heure + ". Motif : " + motif);
    }

    @Override
    public void sendBulletinDisponible(String telephone, String eleveNom, String trimestre, String lien) {
        sendMessage(telephone, "Le bulletin de " + eleveNom + " pour " + trimestre + " est disponible : " + lien);
    }

    @Override
    public void sendRappelPaiement(String telephone, String montantDu, String echeance) {
        sendMessage(telephone, "Rappel paiement NouraSchool : montant du " + montantDu + ", echeance " + echeance + ".");
    }

    private void sendMessage(String telephone, String message) {
        if (!enabled) {
            LOG.infov("WhatsApp gateway disabled, message not sent to={0}", maskPhone(telephone));
            return;
        }
        if (gatewayApiKey == null || gatewayApiKey.isBlank()) {
            throw new IllegalStateException("WHATSAPP_GATEWAY_API_KEY is not configured");
        }
        if (telephone == null || telephone.isBlank()) {
            throw new IllegalArgumentException("Telephone is required for WhatsApp message");
        }

        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "to", normalizePhone(telephone),
                    "message", message
            ));
            HttpRequest request = HttpRequest.newBuilder(URI.create(gatewayUrl.replaceAll("/$", "") + "/send"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .header("X-API-Key", gatewayApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOG.warnv("WhatsApp gateway failed status={0} to={1} body={2}",
                        response.statusCode(), maskPhone(telephone), response.body());
                throw new IllegalStateException("WhatsApp gateway failed with status " + response.statusCode());
            }
            LOG.infov("WhatsApp message sent to={0}", maskPhone(telephone));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize WhatsApp payload", e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to call WhatsApp gateway", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("WhatsApp gateway call interrupted", e);
        }
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("[^0-9+]", "");
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return phone.substring(0, Math.min(4, phone.length())) + "***";
    }
}
