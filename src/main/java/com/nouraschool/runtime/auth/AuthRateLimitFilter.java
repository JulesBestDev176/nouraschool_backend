package com.nouraschool.runtime.auth;

import com.nouraschool.domain.exception.codes.ErrorDto;
import com.nouraschool.domain.services.RedisService;
import com.nouraschool.runtime.correlation.CorrelationContext;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * Rate limiting sur les endpoints auth (Redis) :
 * - 5 tentatives login / IP / 10 min
 */
@Provider
@Priority(Priorities.AUTHENTICATION - 200)
public class AuthRateLimitFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(AuthRateLimitFilter.class);
    private static final String REDIS_KEY_LOGIN = "auth:ratelimit:login:";

    @Inject
    RedisService redisService;

    @Inject
    CorrelationContext correlationContext;

    @ConfigProperty(name = "app.auth.rate-limit.login-per-ip", defaultValue = "5")
    int loginPerIp;

    @ConfigProperty(name = "app.auth.rate-limit.login-window-seconds", defaultValue = "600")
    long loginWindowSeconds;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getRequest().getMethod();
        if (!"POST".equalsIgnoreCase(method) || !path.contains("auth/login")) {
            return;
        }

        String ip = resolveClientIp(requestContext);
        String key = REDIS_KEY_LOGIN + sanitizeIp(ip);
        int count = getCount(key);
        if (count >= loginPerIp) {
            LOG.warnv("Auth rate limit rejected path={0} ip={1} count={2} limit={3} correlationId={4}",
                    path,
                    ip,
                    count,
                    loginPerIp,
                    correlationContext != null ? correlationContext.getCorrelationId() : null);
            requestContext.abortWith(build429Response());
            return;
        }
        incrementCount(key);
        LOG.debugv("Auth rate limit counted path={0} ip={1} count={2} limit={3}",
                path,
                ip,
                count + 1,
                loginPerIp);
    }

    private String resolveClientIp(ContainerRequestContext requestContext) {
        String forwarded = requestContext.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            String first = forwarded.split(",")[0].trim();
            if (!first.isBlank()) return first;
        }
        String realIp = requestContext.getHeaderString("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) return realIp.trim();
        return "unknown";
    }

    private static String sanitizeIp(String ip) {
        if (ip == null || ip.isBlank()) return "unknown";
        return ip.replaceAll("[^a-zA-Z0-9.:\\[\\]-]", "_");
    }

    private int getCount(String key) {
        String v = redisService.get(key);
        if (v == null || v.isBlank()) return 0;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void incrementCount(String key) {
        int current = getCount(key);
        redisService.set(key, String.valueOf(current + 1), loginWindowSeconds);
    }

    private Response build429Response() {
        String correlationId = correlationContext != null && correlationContext.getCorrelationId() != null
                ? correlationContext.getCorrelationId()
                : UUID.randomUUID().toString();
        ErrorDto dto = ErrorDto.builder()
                .code("TOO_MANY_REQUESTS")
                .message("Trop de tentatives, réessayez plus tard")
                .details(List.of())
                .correlationId(correlationId)
                .timestamp(Instant.now().atOffset(ZoneOffset.UTC).toString())
                .build();
        return Response.status(429)
                .type(MediaType.APPLICATION_JSON)
                .entity(dto)
                .build();
    }
}
