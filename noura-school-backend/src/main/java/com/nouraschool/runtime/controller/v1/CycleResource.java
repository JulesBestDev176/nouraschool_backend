package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.etablissement.CycleDto;
import com.nouraschool.domain.services.CycleService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/cycles")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CycleResource {

    @Inject
    CycleService cycleService;

    @GET
    public List<CycleDto> findAll() {
        return cycleService.findAll();
    }

    @GET
    @Path("/{id}")
    public CycleDto findById(@PathParam("id") String id) {
        return cycleService.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid CycleDto dto) {
        return Response.status(Response.Status.CREATED).entity(cycleService.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public CycleDto update(@PathParam("id") String id, @Valid CycleDto dto) {
        return cycleService.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        cycleService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
