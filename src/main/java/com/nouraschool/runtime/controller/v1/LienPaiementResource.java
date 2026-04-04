package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.inscription.LienPaiementCreateDto;
import com.nouraschool.domain.dtos.inscription.LienPaiementParentDto;
import com.nouraschool.domain.services.LienPaiementService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Map;

@Path("/api/v1/liens-paiement")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LienPaiementResource {

    @Inject
    LienPaiementService lienPaiementService;

    @POST
    @Path("/generer")
    @RolesAllowed({"ADMIN", "CAISSIER"})
    @SecurityRequirement(name = "Bearer")
    public Response generer(List<@Valid LienPaiementCreateDto> dtos) {
        return Response.status(Response.Status.CREATED).entity(lienPaiementService.generate(dtos)).build();
    }

    @GET
    @Path("/{token}/detail")
    @PermitAll
    public LienPaiementParentDto getDetail(@PathParam("token") String token) {
        return lienPaiementService.findByToken(token);
    }

    @POST
    @Path("/{token}/verifier-otp")
    @PermitAll
    public LienPaiementParentDto verifierOtp(@PathParam("token") String token, Map<String, String> body) {
        String otp = body != null ? body.get("otp") : null;
        return lienPaiementService.verifierOtp(token, otp);
    }

    @POST
    @Path("/{token}/payer")
    @PermitAll
    public LienPaiementParentDto payer(@PathParam("token") String token) {
        return lienPaiementService.payer(token);
    }
}
