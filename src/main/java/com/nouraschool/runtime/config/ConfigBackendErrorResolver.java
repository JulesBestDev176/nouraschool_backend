package com.nouraschool.runtime.config;

import com.nouraschool.domain.exception.BackendErrorResolver;
import com.nouraschool.domain.exception.codes.BackendError;
import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Résolution des codes d'erreur depuis application.yml / ApplicationProperties.
 * Remplace le fichier CSV (gestion des erreurs avec codes spécifiques).
 */
@ApplicationScoped
@DefaultBean
public class ConfigBackendErrorResolver implements BackendErrorResolver {

    @Inject
    ApplicationProperties applicationProperties;

    @Override
    public BackendError resolveByCodeName(String internalCodeName) {
        BackendError error = applicationProperties.getErrorByCode(internalCodeName);
        if (error != null) {
            return error;
        }
        BackendError fallback = new BackendError();
        fallback.setInternalNameCode(internalCodeName);
        fallback.setInternalMessage("Erreur: " + internalCodeName);
        fallback.setInternalCode(500);
        fallback.setHttpCode(500);
        return fallback;
    }
}
