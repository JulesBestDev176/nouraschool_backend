package com.nouraschool.tenant;

import com.nouraschool.domain.entities.AdministrateurEntity;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Crée un second tenant et un utilisateur pour les tests d'isolation cross-tenant.
 */
@ApplicationScoped
public class TenantTestDataHelper {

    private static final String TENANT2_SLUG = "tenant-test-iso";
    private static final String USER_TENANT2_USERNAME = "user-tenant2";
    private static final String USER_TENANT2_EMAIL = "user2@tenant2.test";

    @Inject
    TenantRepository tenantRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    PasswordEncoder passwordEncoder;

    @Transactional
    public void createSecondTenantAndUser() {
        if (tenantRepository.findBySlug(TENANT2_SLUG).isPresent()) {
            return;
        }
        TenantEntity tenant2 = new TenantEntity();
        tenant2.slug = TENANT2_SLUG;
        tenant2.nom = "Tenant Test Isolation";
        tenant2.plan = "TRIAL";
        tenant2.actif = true;
        tenant2.persist();

        if (userRepository.findByUsername(USER_TENANT2_USERNAME) != null) {
            return;
        }
        AdministrateurEntity user2 = new AdministrateurEntity();
        user2.tenant = tenant2;
        user2.username = USER_TENANT2_USERNAME;
        user2.email = USER_TENANT2_EMAIL;
        user2.password = passwordEncoder.encode("Test123!");
        user2.firstName = "User";
        user2.lastName = "Tenant2";
        user2.telephone = "+221000000001";
        user2.adresse = "Adresse";
        user2.role = UserRole.ADMIN;
        user2.active = true;
        user2.mustChangePassword = true;
        userRepository.persist(user2);
    }
}
