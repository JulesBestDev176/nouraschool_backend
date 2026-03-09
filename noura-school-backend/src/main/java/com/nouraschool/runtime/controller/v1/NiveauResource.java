package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.etablissement.NiveauDto;
import com.nouraschool.domain.services.NiveauService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/api/v1/niveaux")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NiveauResource {

    @Inject
    NiveauService niveauService;

    @GET
    public List<NiveauDto> findAll(@QueryParam("cycleId") String cycleId) {
        Optional<UUID> cycleUuid = Optional.ofNullable(cycleId)
                .filter(s -> !s.isBlank())
                .map(UUID::fromString);
        return niveauService.findAll(cycleUuid);
    }

    @GET
    @Path("/{id}")
    public NiveauDto findById(@PathParam("id") String id) {
        return niveauService.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid NiveauDto dto) {
        return Response.status(Response.Status.CREATED).entity(niveauService.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public NiveauDto update(@PathParam("id") String id, @Valid NiveauDto dto) {
        return niveauService.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        niveauService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
