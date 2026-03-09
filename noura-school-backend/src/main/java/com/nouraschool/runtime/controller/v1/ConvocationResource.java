package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.viescolaire.ConvocationCreateDto;
import com.nouraschool.domain.dtos.viescolaire.ConvocationDto;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.services.ConvocationService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/api/v1/convocations")
@RolesAllowed({"ADMIN", "SURVEILLANT"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConvocationResource {

    @Inject
    ConvocationService convocationService;

    @POST
    public Response create(@Valid ConvocationCreateDto dto, @Context SecurityContext securityContext) {
        UUID creePar = CurrentUser.getUserId(securityContext)
                .orElseThrow(() -> new InvalidRequestException("Utilisateur non authentifié"));
        return Response.status(Response.Status.CREATED).entity(convocationService.create(dto, creePar)).build();
    }

    @GET
    public List<ConvocationDto> findAll() {
        return convocationService.findAll();
    }

    @GET
    @Path("/{id}")
    public ConvocationDto findById(@PathParam("id") String id) {
        return convocationService.findById(UUID.fromString(id));
    }

    @PATCH
    @Path("/{id}/compte-rendu")
    public ConvocationDto updateCompteRendu(@PathParam("id") String id, Map<String, String> body) {
        String compteRendu = body != null ? body.get("compteRendu") : null;
        return convocationService.updateCompteRendu(UUID.fromString(id), compteRendu);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        convocationService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
