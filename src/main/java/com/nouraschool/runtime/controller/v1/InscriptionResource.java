package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.inscription.InscriptionCreateDto;
import com.nouraschool.domain.dtos.inscription.InscriptionDto;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.services.InscriptionService;
import com.nouraschool.runtime.security.CurrentUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.Map;
import java.util.UUID;

@Path("/api/v1/inscriptions")
@RolesAllowed({"ADMIN", "CAISSIER"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InscriptionResource {

    @Inject
    InscriptionService inscriptionService;

    @POST
    public Response create(@Valid InscriptionCreateDto dto, @Context SecurityContext securityContext) {
        UUID creePar = CurrentUser.getUserId(securityContext)
                .orElseThrow(() -> new InvalidRequestException("Utilisateur non authentifié"));
        return Response.status(Response.Status.CREATED).entity(inscriptionService.create(dto, creePar)).build();
    }

    @GET
    public java.util.List<InscriptionDto> findAll() {
        return inscriptionService.findAll();
    }

    @GET
    @Path("/{id}")
    public InscriptionDto findById(@PathParam("id") String id) {
        return inscriptionService.findById(UUID.fromString(id));
    }

    @PATCH
    @Path("/{id}/transferer")
    public InscriptionDto transferer(@PathParam("id") String id, Map<String, UUID> body) {
        UUID nouvelleClasseId = body.get("classeId");
        if (nouvelleClasseId == null) {
            throw new InvalidRequestException("VALIDATION_ECHOUEE");
        }
        return inscriptionService.transferer(UUID.fromString(id), nouvelleClasseId);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        inscriptionService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
