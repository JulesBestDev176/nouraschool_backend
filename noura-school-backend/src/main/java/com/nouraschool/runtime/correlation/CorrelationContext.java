package com.nouraschool.runtime.correlation;

import jakarta.enterprise.context.RequestScoped;

/**
 * Contexte request-scoped pour l'ID de corrélation de la requête.
 * Renseigné par {@link CorrelationFilter}, utilisé par le handler d'erreurs et les logs (MDC).
 */
@RequestScoped
public class CorrelationContext {

    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
