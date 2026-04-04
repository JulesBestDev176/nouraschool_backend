package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.etablissement.SalleDto;
import com.nouraschool.domain.services.SalleService;
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

@Path("/api/v1/salles")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SalleResource {

    @Inject
    SalleService salleService;

    @GET
    public List<SalleDto> findAll(@QueryParam("batimentId") String batimentId) {
        Optional<UUID> batimentUuid = Optional.ofNullable(batimentId)
                .filter(s -> !s.isBlank())
                .map(UUID::fromString);
        return salleService.findAll(batimentUuid);
    }

    @GET
    @Path("/{id}")
    public SalleDto findById(@PathParam("id") String id) {
        return salleService.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid SalleDto dto) {
        return Response.status(Response.Status.CREATED).entity(salleService.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public SalleDto update(@PathParam("id") String id, @Valid SalleDto dto) {
        return salleService.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        salleService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
