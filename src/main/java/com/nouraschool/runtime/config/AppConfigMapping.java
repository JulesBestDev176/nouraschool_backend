package com.nouraschool.runtime.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Configuration centrale (application.yml) — auth, logging, paiement, bcrypt.
 * Les codes d'erreur sont gérés par ApplicationProperties (map par défaut).
 */
@ApplicationScoped
public class AppConfigMapping {

    @Inject
    @ConfigProperty(name = "app.auth.phone-country-codes", defaultValue = "+221,+222,+223,+224,+220,+245,+225")
    String phoneCountryCodes;

    @Inject
    @ConfigProperty(name = "app.logging.elastic.enabled", defaultValue = "false")
    boolean elasticEnabled;

    @Inject
    @ConfigProperty(name = "app.logging.elastic.host", defaultValue = "localhost")
    String elasticHost;

    @Inject
    @ConfigProperty(name = "app.logging.elastic.port", defaultValue = "9200")
    int elasticPort;

    @Inject
    @ConfigProperty(name = "app.logging.elastic.index", defaultValue = "noura-audit")
    String elasticIndex;

    @Inject
    @ConfigProperty(name = "app.payment.provider", defaultValue = "payetech")
    String paymentProvider;

    @Inject
    @ConfigProperty(name = "app.bcrypt.cost", defaultValue = "12")
    int bcryptCost;

    /** Indicatifs téléphone (Sénégal, Mauritanie, Mali, Guinée, Gambie, Bissau, Côte d'Ivoire). */
    public List<String> getPhoneCountryCodesList() {
        if (phoneCountryCodes == null || phoneCountryCodes.isBlank()) return List.of();
        return List.of(phoneCountryCodes.split(","));
    }

    public boolean isElasticEnabled() { return elasticEnabled; }
    public String getElasticHost() { return elasticHost; }
    public int getElasticPort() { return elasticPort; }
    public String getElasticIndex() { return elasticIndex; }
    public String getPaymentProvider() { return paymentProvider; }
    public int getBcryptCost() { return bcryptCost; }
}
