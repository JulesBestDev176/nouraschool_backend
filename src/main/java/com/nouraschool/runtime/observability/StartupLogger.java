package com.nouraschool.runtime.observability;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StartupLogger {

    private static final Logger LOG = Logger.getLogger(StartupLogger.class);

    @ConfigProperty(name = "quarkus.profile", defaultValue = "prod")
    String profile;

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8080")
    int httpPort;

    @ConfigProperty(name = "quarkus.datasource.jdbc.url", defaultValue = "not-configured")
    String jdbcUrl;

    @ConfigProperty(name = "quarkus.redis.hosts", defaultValue = "not-configured")
    String redisHosts;

    @ConfigProperty(name = "app.security.jwt-validation-enabled", defaultValue = "true")
    boolean jwtValidationEnabled;

    @ConfigProperty(name = "app.logging.elastic.enabled", defaultValue = "false")
    boolean elasticLoggingEnabled;

    void onStart(@Observes StartupEvent event) {
        LOG.infov(
                "Application startup completed profile={0} httpPort={1} datasource={2} redis={3} jwtValidationEnabled={4} elasticLoggingEnabled={5}",
                profile,
                httpPort,
                sanitizeJdbcUrl(jdbcUrl),
                sanitizeRedisHosts(redisHosts),
                jwtValidationEnabled,
                elasticLoggingEnabled
        );
    }

    void onStop(@Observes ShutdownEvent event) {
        LOG.infov("Application shutdown requested profile={0}", profile);
    }

    private static String sanitizeJdbcUrl(String url) {
        if (url == null || url.isBlank()) {
            return "not-configured";
        }
        return url.replaceAll("(?i)(password=)[^&;]+", "$1***");
    }

    private static String sanitizeRedisHosts(String hosts) {
        if (hosts == null || hosts.isBlank()) {
            return "not-configured";
        }
        return hosts.replaceAll("(?i)(redis://)([^:@/]+:)?([^@/]+)@", "$1***@");
    }
}
