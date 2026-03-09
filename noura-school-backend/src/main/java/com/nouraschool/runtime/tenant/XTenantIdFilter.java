package com.nouraschool.runtime.tenant;

import com.nouraschool.domain.constants.Constants;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.repositories.TenantRepository;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.UUID;

/**
 * Résout le tenant depuis le header {@code X-Tenant-Id} (UUID) et le fixe dans {@link TenantContext}.
 * Si le header est absent, utilise le tenant par défaut.
 * Si le header est présent mais invalide (UUID invalide, tenant inexistant ou inactif), répond 403.
 */
@Provider
@Priority(Priorities.AUTHENTICATION - 100)
public class XTenantIdFilter implements ContainerRequestFilter {

    @Inject
    TenantContext tenantContext;

    @Inject
    TenantRepository tenantRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (!path.startsWith(Constants.API_BASE_PATH)) {
            tenantContext.setTenantId(tenantRepository.findDefault().id);
            return;
        }

        String headerValue = requestContext.getHeaderString(Constants.HEADER_TENANT_ID);
        if (headerValue == null || headerValue.isBlank()) {
            tenantContext.setTenantId(tenantRepository.findDefault().id);
            return;
        }

        UUID tenantId;
        try {
            tenantId = UUID.fromString(headerValue.trim());
        } catch (IllegalArgumentException e) {
            abort(requestContext, 403, "TENANT_INVALIDE", "X-Tenant-Id invalide (UUID attendu)");
            return;
        }

        var tenantOpt = tenantRepository.findById(tenantId);
        if (tenantOpt.isEmpty()) {
            abort(requestContext, 403, "TENANT_INACTIF", "Établissement introuvable");
            return;
        }
        TenantEntity tenant = tenantOpt.get();
        if (!Boolean.TRUE.equals(tenant.actif)) {
            abort(requestContext, 403, "TENANT_INACTIF", "Établissement suspendu");
            return;
        }

        tenantContext.setTenantId(tenant.id);
    }

    private void abort(ContainerRequestContext requestContext, int status, String code, String message) {
        requestContext.abortWith(
                Response.status(status)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new TenantError(code, message))
                        .build());
    }

    record TenantError(String code, String message) {
    }
}
