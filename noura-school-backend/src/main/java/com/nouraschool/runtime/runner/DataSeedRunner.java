package com.nouraschool.runtime.runner;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class DataSeedRunner {

    @Inject
    DataSeedService dataSeedService;

    void onStart(@Observes StartupEvent event) {
        dataSeedService.seedAdminIfAbsent();
    }
}
