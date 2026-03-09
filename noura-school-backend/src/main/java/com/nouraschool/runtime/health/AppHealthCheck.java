package com.nouraschool.runtime.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Health checks pour /q/health/live et /q/health/ready (task.md FOLDER 13).
 */
@Readiness
@ApplicationScoped
public class AppHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("app-ready").up().build();
    }
}
