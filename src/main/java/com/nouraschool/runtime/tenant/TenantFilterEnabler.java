package com.nouraschool.runtime.tenant;

import com.nouraschool.domain.repositories.TenantRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

import java.util.UUID;

/**
 * Active le filtre Hibernate "tenantFilter" sur la session pour isoler les données par tenant.
 * À appeler au début des méthodes du repository qui interrogent des entités avec ce filtre.
 */
@ApplicationScoped
public class TenantFilterEnabler {

    public static final String FILTER_NAME = "tenantFilter";
    public static final String PARAM_TENANT_ID = "tenantId";

    @Inject
    TenantContext tenantContext;

    @Inject
    TenantRepository tenantRepository;

    public void enableTenantFilter(EntityManager entityManager) {
        if (entityManager == null) return;
        UUID tenantId = tenantContext.hasTenant()
                ? tenantContext.getTenantId()
                : tenantRepository.findDefault().id;
        Session session = entityManager.unwrap(Session.class);
        if (session != null) {
            session.enableFilter(FILTER_NAME).setParameter(PARAM_TENANT_ID, tenantId);
        }
    }
}
