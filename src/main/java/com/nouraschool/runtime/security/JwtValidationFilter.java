package com.nouraschool.runtime.security;

import com.nouraschool.domain.constants.Constants;
import com.nouraschool.domain.services.JwtService;
import com.nouraschool.runtime.config.ApplicationProperties;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtValidationFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(JwtValidationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final List<String> PUBLIC_PATHS = List.of(
            Constants.LOGIN_PATH,
            Constants.REFRESH_PATH,
            Constants.SWAGGER_UI_PATH,
            Constants.OPENAPI_PATH,
            Constants.HEALTH_PATH
    );

    @Inject
    JwtService jwtService;

    @Inject
    ApplicationProperties applicationProperties;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!applicationProperties.security().jwtValidationEnabled()) {
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        if (isPublicPath(normalizedPath)) {
            return;
        }
        if (!normalizedPath.startsWith(Constants.API_BASE_PATH)) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            LOG.warnv("JWT rejected reason=MISSING_TOKEN method={0} path={1}",
                    requestContext.getMethod(),
                    normalizedPath);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ErrorResponse("MISSING_TOKEN", "Token JWT requis"))
                    .build());
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (!jwtService.isValid(token)) {
            LOG.warnv("JWT rejected reason=INVALID_TOKEN method={0} path={1}",
                    requestContext.getMethod(),
                    normalizedPath);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ErrorResponse("INVALID_TOKEN", "Token JWT invalide ou expiré"))
                    .build());
            return;
        }
    }

    private boolean isPublicPath(String normalizedPath) {
        return PUBLIC_PATHS.stream()
                .map(p -> p.startsWith("/") ? p : "/" + p)
                .anyMatch(p -> normalizedPath.startsWith(p));
    }

    record ErrorResponse(String code, String message) {
    }
}
