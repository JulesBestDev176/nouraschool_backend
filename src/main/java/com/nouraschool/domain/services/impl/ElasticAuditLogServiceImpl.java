package com.nouraschool.domain.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nouraschool.domain.services.AuditLogElasticSender;
import com.nouraschool.runtime.config.AppConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Envoi des logs d'audit vers Elasticsearch (app.logging.elastic.*).
 * Si désactivé, les appels sont no-op. Appelé par AuditLogServiceImpl lorsque Elastic est activé.
 */
@ApplicationScoped
public class ElasticAuditLogServiceImpl implements AuditLogElasticSender {

    private static final Logger LOG = Logger.getLogger(ElasticAuditLogServiceImpl.class);

    @Inject
    AppConfigMapping appConfig;

    @Inject
    ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    @Override
    public void log(String action, UUID tenantId, UUID utilisateurId, String role,
                    String resourceType, UUID resourceId, Map<String, Object> details,
                    String ipAddress, String userAgent) {
        if (!appConfig.isElasticEnabled()) {
            return;
        }
        try {
            String index = appConfig.getElasticIndex();
            String url = "http://" + appConfig.getElasticHost() + ":" + appConfig.getElasticPort()
                    + "/" + index + "/_doc";
            Map<String, Object> doc = new HashMap<>();
            doc.put("action", action);
            doc.put("tenant_id", tenantId != null ? tenantId.toString() : null);
            doc.put("utilisateur_id", utilisateurId != null ? utilisateurId.toString() : null);
            doc.put("role", role);
            doc.put("resource_type", resourceType);
            doc.put("resource_id", resourceId != null ? resourceId.toString() : null);
            doc.put("details", details != null ? details : Map.of());
            doc.put("ip_address", ipAddress);
            doc.put("user_agent", userAgent);
            doc.put("@timestamp", Instant.now().toString());

            String body = objectMapper.writeValueAsString(doc);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                LOG.warnf("[ElasticAuditLogService][log] Elasticsearch returned %d for action=%s", response.statusCode(), action);
            }
        } catch (Exception e) {
            LOG.error("[ElasticAuditLogService][log] Failed to send audit log to Elasticsearch action=" + action, e);
        }
    }

}
