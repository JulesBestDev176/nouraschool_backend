package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.viescolaire.LienBulletinParentDto;
import com.nouraschool.domain.services.LienBulletinService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.Map;
import java.util.UUID;

@Path("/api/v1/liens-bulletin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LienBulletinResource {

    @Inject
    LienBulletinService lienBulletinService;

    @POST
    @Path("/generer")
    @RolesAllowed({"ADMIN", "SURVEILLANT"})
    @SecurityRequirement(name = "Bearer")
    public Response generer(Map<String, String> body) {
        UUID bulletinId = UUID.fromString(body.get("bulletinId"));
        UUID parentId = UUID.fromString(body.get("parentId"));
        return Response.status(Response.Status.CREATED).entity(lienBulletinService.generate(bulletinId, parentId)).build();
    }

    @GET
    @Path("/{token}/consulter")
    @PermitAll
    public LienBulletinParentDto consulter(@PathParam("token") String token) {
        return lienBulletinService.findByToken(token);
    }

    @POST
    @Path("/{token}/verifier-otp")
    @PermitAll
    public LienBulletinParentDto verifierOtp(@PathParam("token") String token, Map<String, String> body) {
        String otp = body != null ? body.get("otp") : null;
        return lienBulletinService.verifierOtp(token, otp);
    }
}
