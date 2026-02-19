package com.nouraschool.runtime.runner;

import com.nouraschool.domain.entities.AdministrateurEntity;
import com.nouraschool.domain.enums.UserRole;
import com.nouraschool.domain.repositories.UserRepository;
import com.nouraschool.domain.services.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DataSeedService {

    private static final Logger LOG = Logger.getLogger(DataSeedService.class);

    @ConfigProperty(name = "app.seed.enabled", defaultValue = "true")
    boolean seedEnabled;

    @ConfigProperty(name = "app.seed.admin.username", defaultValue = "admin")
    String adminUsername;

    @ConfigProperty(name = "app.seed.admin.password", defaultValue = "Admin123!")
    String adminPassword;

    @ConfigProperty(name = "app.seed.admin.email", defaultValue = "admin@noura-school.com")
    String adminEmail;

    @ConfigProperty(name = "app.seed.admin.first-name", defaultValue = "Admin")
    String adminFirstName;

    @ConfigProperty(name = "app.seed.admin.last-name", defaultValue = "System")
    String adminLastName;

    @ConfigProperty(name = "app.seed.admin.telephone", defaultValue = "+221000000000")
    String adminTelephone;

    @ConfigProperty(name = "app.seed.admin.adresse", defaultValue = "Siège")
    String adminAdresse;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Transactional
    public void seedAdminIfAbsent() {
        if (!seedEnabled) {
            LOG.debug("Seed disabled (app.seed.enabled=false), skipping.");
            return;
        }
        if (userRepository.findByUsername(adminUsername) != null) {
            LOG.debugf("Admin user '%s' already exists, skipping seed.", adminUsername);
            return;
        }
        AdministrateurEntity admin = new AdministrateurEntity();
        admin.username = adminUsername;
        admin.email = adminEmail;
        admin.password = passwordEncoder.encode(adminPassword);
        admin.firstName = adminFirstName;
        admin.lastName = adminLastName;
        admin.telephone = adminTelephone;
        admin.adresse = adminAdresse;
        admin.role = UserRole.ADMIN;
        admin.active = true;
        userRepository.persist(admin);
        LOG.infof("Seed: admin user created (username=%s). Change password in production.", adminUsername);
    }
}
