package com.nouraschool.runtime.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ApplicationProperties {

    @ConfigProperty(name = "app.jwt.issuer", defaultValue = "https://noura-school.com")
    String jwtIssuer;

    @ConfigProperty(name = "app.jwt.audience", defaultValue = "noura-school-api")
    String jwtAudience;

    @ConfigProperty(name = "app.jwt.access-token-lifespan", defaultValue = "900")
    long accessTokenLifespan;

    @ConfigProperty(name = "app.security.jwt-validation-enabled", defaultValue = "true")
    boolean jwtValidationEnabled;

    @ConfigProperty(name = "app.security.clock-skew-seconds", defaultValue = "60")
    int clockSkewSeconds;

    public JwtConfig jwt() {
        return new JwtConfig(jwtIssuer, jwtAudience, accessTokenLifespan);
    }

    public SecurityConfig security() {
        return new SecurityConfig(jwtValidationEnabled, clockSkewSeconds);
    }

    public record JwtConfig(String issuer, String audience, long accessTokenLifespan) {
    }

    public record SecurityConfig(boolean jwtValidationEnabled, int clockSkewSeconds) {
    }
}
