package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.ChangePasswordRequest;
import com.nouraschool.domain.dtos.ForgotPasswordRequest;
import com.nouraschool.domain.dtos.LoginRequest;
import com.nouraschool.domain.dtos.LoginResponse;
import com.nouraschool.domain.dtos.RefreshRequest;
import com.nouraschool.domain.dtos.ResetPasswordRequest;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.UUID;

@Path("/api/v1/auth")
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
    @Operation(summary = "Connexion", description = "Authentification par login (email/username) et mot de passe. Retourne accessToken et refreshToken.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Connexion réussie"),
            @APIResponse(responseCode = "401", description = "Identifiants invalides"),
            @APIResponse(responseCode = "423", description = "Compte verrouillé")
    })
    public LoginResponse login(@Valid LoginRequest request) {
        return authService.login(request);
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public LoginResponse refresh(@Valid RefreshRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @GET
    @Path("/me")
    @RolesAllowed({"ADMIN", "CAISSIER", "SURVEILLANT", "ENSEIGNANT", "ELEVE", "PARENT"})
    @SecurityRequirement(name = "Bearer")
    public com.nouraschool.domain.dtos.AuthMeDto me(@Context SecurityContext securityContext) {
        return authService.me(securityContext);
    }

    @POST
    @Path("/logout")
    @RolesAllowed({"ADMIN", "CAISSIER", "SURVEILLANT", "ENSEIGNANT", "ELEVE", "PARENT"})
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

    @POST
    @Path("/forgot-password")
    @PermitAll
    public Response forgotPassword(@Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return Response.noContent().build();
    }

    @POST
    @Path("/reset-password")
    @PermitAll
    public Response resetPassword(@Valid ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return Response.noContent().build();
    }

    @POST
    @Path("/change-password")
    @RolesAllowed({"ADMIN", "CAISSIER", "SURVEILLANT", "ENSEIGNANT", "ELEVE", "PARENT"})
    @SecurityRequirement(name = "Bearer")
    public Response changePassword(@Context SecurityContext securityContext, @Valid ChangePasswordRequest request) {
        UUID userId = com.nouraschool.runtime.security.CurrentUser.getUserId(securityContext)
                .orElseThrow(() -> new com.nouraschool.domain.exception.errors.InvalidRequestException("ACCES_REFUSE"));
        authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Response.noContent().build();
    }
}
