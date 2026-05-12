package com.nouraschool.runtime.runner;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DataSeedRunner {

    private static final Logger LOG = Logger.getLogger(DataSeedRunner.class);

    @Inject
    DataSeedService dataSeedService;

    @Inject
    TestDataSeeder testDataSeeder;

    @ConfigProperty(name = "quarkus.profile", defaultValue = "prod")
    String profile;

    void onStart(@Observes StartupEvent event) {
        LOG.infov("Data seed startup step started profile={0}", profile);
        dataSeedService.seedAdminIfAbsent();
        if ("dev".equals(profile)) {
            LOG.info("Development profile detected; checking test data seed");
            testDataSeeder.seedIfEmpty();
        }
        LOG.infov("Data seed startup step completed profile={0}", profile);
    }
}
