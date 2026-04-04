package com.nouraschool.runtime.runner;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DataSeedRunner {

    @Inject
    DataSeedService dataSeedService;

    @Inject
    TestDataSeeder testDataSeeder;

    @ConfigProperty(name = "quarkus.profile", defaultValue = "prod")
    String profile;

    void onStart(@Observes StartupEvent event) {
        dataSeedService.seedAdminIfAbsent();
        if ("dev".equals(profile)) {
            testDataSeeder.seedIfEmpty();
        }
    }
}
