package com.nouraschool.runtime.runner;

import com.nouraschool.domain.entities.AdministrateurEntity;
import com.nouraschool.domain.entities.PlateformeUtilisateurEntity;
import com.nouraschool.domain.entities.TenantEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.repositories.PlateformeUtilisateurRepository;
import com.nouraschool.domain.repositories.TenantRepository;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DataSeedService {

    private static final Logger LOG = Logger.getLogger(DataSeedService.class);

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin123!";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@noura-school.com";
    private static final String DEFAULT_ADMIN_FIRST_NAME = "Admin";
    private static final String DEFAULT_ADMIN_LAST_NAME = "System";
    private static final String DEFAULT_ADMIN_TELEPHONE = "+221000000000";
    private static final String DEFAULT_ADMIN_ADRESSE = "Siège";
    private static final String DEFAULT_PLATFORM_EMAIL = "superadmin@noura-school.com";
    private static final String DEFAULT_PLATFORM_PASSWORD = "SuperAdmin123!";

    @Inject
    UserRepository userRepository;
    @Inject
    PlateformeUtilisateurRepository plateformeUtilisateurRepository;
    @Inject
    TenantRepository tenantRepository;
    @Inject
    PasswordEncoder passwordEncoder;

    @Transactional
    public void seedAdminIfAbsent() {
        seedPlatformAdminIfAbsent();
        if (userRepository.findByUsername(DEFAULT_ADMIN_USERNAME) != null) {
            LOG.debugf("Admin user '%s' already exists, skipping seed.", DEFAULT_ADMIN_USERNAME);
            return;
        }
        TenantEntity defaultTenant = tenantRepository.findDefault();
        AdministrateurEntity admin = new AdministrateurEntity();
        admin.tenant = defaultTenant;
        admin.username = DEFAULT_ADMIN_USERNAME;
        admin.email = DEFAULT_ADMIN_EMAIL;
        admin.password = passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD);
        admin.firstName = DEFAULT_ADMIN_FIRST_NAME;
        admin.lastName = DEFAULT_ADMIN_LAST_NAME;
        admin.telephone = DEFAULT_ADMIN_TELEPHONE;
        admin.adresse = DEFAULT_ADMIN_ADRESSE;
        admin.role = UserRole.ADMIN;
        admin.active = true;
        admin.mustChangePassword = true;
        userRepository.persist(admin);
        LOG.infof("Seed: admin user created (username=%s). Change password in production.", DEFAULT_ADMIN_USERNAME);
    }

    private void seedPlatformAdminIfAbsent() {
        if (plateformeUtilisateurRepository.findByEmail(DEFAULT_PLATFORM_EMAIL).isPresent()) {
            LOG.debugf("Platform admin '%s' already exists, skipping seed.", DEFAULT_PLATFORM_EMAIL);
            return;
        }

        PlateformeUtilisateurEntity platformAdmin = new PlateformeUtilisateurEntity();
        platformAdmin.nom = "Platform";
        platformAdmin.prenom = "Admin";
        platformAdmin.email = DEFAULT_PLATFORM_EMAIL;
        platformAdmin.motDePasse = passwordEncoder.encode(DEFAULT_PLATFORM_PASSWORD);
        platformAdmin.rolePlateforme = "SUPER_ADMIN";
        platformAdmin.actif = true;
        plateformeUtilisateurRepository.persist(platformAdmin);
        LOG.infof("Seed: platform admin created (email=%s). Change password in production.", DEFAULT_PLATFORM_EMAIL);
    }
}
