package com.nouraschool.runtime.tenant;

import jakarta.enterprise.context.RequestScoped;

import java.util.UUID;

/**
 * Contexte tenant pour la requête en cours.
 * Renseigné par le filtre X-Tenant-Id (ou tenant par défaut si absent).
 */
@RequestScoped
public class TenantContext {

    private UUID tenantId;

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public boolean hasTenant() {
        return tenantId != null;
    }
}
