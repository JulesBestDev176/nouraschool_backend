package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.LoginRequest;
import com.nouraschool.domain.dtos.LoginResponse;
import com.nouraschool.domain.dtos.RefreshRequest;
import com.nouraschool.domain.services.AuthService;
import com.nouraschool.runtime.aop.Logged;
import com.nouraschool.runtime.aop.Timed;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.UUID;

@Path("/api/auth")
@Logged
@Timed
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    @PermitAll
    public LoginResponse login(@Valid LoginRequest request) {
        return authService.login(request);
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public LoginResponse refresh(@Valid RefreshRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @POST
    @Path("/logout")
    @RolesAllowed({"ADMIN", "CAISSIER", "ENSEIGNANT", "ELEVE", "PARENT"})
    @SecurityRequirement(name = "Bearer")
    public Response logout(@Context SecurityContext securityContext) {
        if (securityContext.getUserPrincipal() instanceof JsonWebToken jwt) {
            Object userIdClaim = jwt.getClaim("userId");
            if (userIdClaim != null) {
                UUID userId = UUID.fromString(userIdClaim.toString());
                authService.logout(userId);
            }
        }
        return Response.noContent().build();
    }
}
