package com.nouraschool.runtime.tenant;

import com.nouraschool.domain.constants.Constants;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.services.JwtService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Résout le tenant depuis le header {@code X-Tenant-Id} (UUID) et le fixe dans {@link TenantContext}.
 * Si le header est absent, utilise le tenant par défaut.
 * Si le header est présent mais invalide (UUID invalide, tenant inexistant ou inactif), répond 403.
 */
@Provider
@Priority(Priorities.AUTHENTICATION - 100)
public class XTenantIdFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(XTenantIdFilter.class);

    @Inject
    TenantContext tenantContext;

    @Inject
    TenantRepository tenantRepository;

    @Inject
    JwtService jwtService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (!path.startsWith(Constants.API_BASE_PATH)) {
            tenantContext.setTenantId(tenantRepository.findDefault().id);
            return;
        }

        String headerValue = requestContext.getHeaderString(Constants.HEADER_TENANT_ID);
        Optional<UUID> tokenTenantId = resolveTokenTenantId(requestContext);
        if (headerValue == null || headerValue.isBlank()) {
            UUID tenantId = tokenTenantId.orElseGet(() -> tenantRepository.findDefault().id);
            validateAndSetTenant(requestContext, tenantId);
            return;
        }

        UUID tenantId;
        try {
            tenantId = UUID.fromString(headerValue.trim());
        } catch (IllegalArgumentException e) {
            LOG.warnv("Tenant rejected reason=INVALID_HEADER path={0} headerValue={1}",
                    path,
                    headerValue);
            abort(requestContext, 403, "TENANT_INVALIDE", "X-Tenant-Id invalide (UUID attendu)");
            return;
        }

        if (tokenTenantId.isPresent() && !tokenTenantId.get().equals(tenantId)) {
            LOG.warnv("Tenant rejected reason=TOKEN_HEADER_MISMATCH path={0} tokenTenantId={1} headerTenantId={2}",
                    path,
                    tokenTenantId.get(),
                    tenantId);
            abort(requestContext, 403, "TENANT_INVALIDE", "X-Tenant-Id ne correspond pas au tenant du token");
            return;
        }

        validateAndSetTenant(requestContext, tenantId);
    }

    private boolean validateAndSetTenant(ContainerRequestContext requestContext, UUID tenantId) {
        var tenantOpt = tenantRepository.findById(tenantId);
        if (tenantOpt.isEmpty()) {
            LOG.warnv("Tenant rejected reason=NOT_FOUND path={0} tenantId={1}",
                    requestContext.getUriInfo().getPath(),
                    tenantId);
            abort(requestContext, 403, "TENANT_INACTIF", "Établissement introuvable");
            return false;
        }
        TenantEntity tenant = tenantOpt.get();
        if (!Boolean.TRUE.equals(tenant.actif)) {
            LOG.warnv("Tenant rejected reason=INACTIVE path={0} tenantId={1}",
                    requestContext.getUriInfo().getPath(),
                    tenantId);
            abort(requestContext, 403, "TENANT_INACTIF", "Établissement suspendu");
            return false;
        }

        tenantContext.setTenantId(tenant.id);
        LOG.debugv("Tenant resolved path={0} tenantId={1}", requestContext.getUriInfo().getPath(), tenant.id);
        return true;
    }

    private Optional<UUID> resolveTokenTenantId(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        return jwtService.parse(token)
                .map(jwt -> jwt.getClaim("tenantId"))
                .filter(claim -> claim != null && !claim.toString().isBlank())
                .flatMap(claim -> parseUuid(claim.toString()));
    }

    private Optional<UUID> parseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
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
