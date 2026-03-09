package com.nouraschool.runtime.correlation;

import com.nouraschool.domain.constants.Constants;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.UUID;

/**
 * Injecte un ID de corrélation (UUID) par requête : MDC + header réponse X-Correlation-Id.
 * Si le client envoie X-Correlation-Id, il est réutilisé pour la chaîne complète.
 */
@Provider
@Priority(1)
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String MDC_KEY = "correlationId";
    private static final String REQUEST_PROPERTY = "correlationId";

    @Inject
    CorrelationContext correlationContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String id = requestContext.getHeaderString(Constants.HEADER_CORRELATION_ID);
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        } else {
            id = id.trim();
        }
        correlationContext.setCorrelationId(id);
        requestContext.setProperty(REQUEST_PROPERTY, id);
        org.slf4j.MDC.put(MDC_KEY, id);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String id = (String) requestContext.getProperty(REQUEST_PROPERTY);
        if (id != null) {
            responseContext.getHeaders().putSingle(Constants.HEADER_CORRELATION_ID, id);
        }
        org.slf4j.MDC.remove(MDC_KEY);
    }
}
