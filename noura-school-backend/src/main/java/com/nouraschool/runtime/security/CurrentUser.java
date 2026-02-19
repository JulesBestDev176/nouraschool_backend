package com.nouraschool.runtime.security;

import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;
import java.util.UUID;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static Optional<UUID> getUserId(SecurityContext securityContext) {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            return Optional.empty();
        }
        if (securityContext.getUserPrincipal() instanceof JsonWebToken jwt) {
            var claim = jwt.getClaim("userId");
            if (claim == null) return Optional.empty();
            return Optional.of(UUID.fromString(claim.toString()));
        }
        return Optional.empty();
    }
}
