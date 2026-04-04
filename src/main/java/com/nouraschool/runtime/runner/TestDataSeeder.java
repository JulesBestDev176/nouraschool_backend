package com.nouraschool.runtime.runner;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TestDataSeeder {

    private static final Logger LOG = Logger.getLogger(TestDataSeeder.class);

    public void seedIfEmpty() {
        // Placeholder dev seeder to satisfy startup wiring.
        LOG.debug("[TestDataSeeder][seedIfEmpty] No-op test data seeding");
    }
}
