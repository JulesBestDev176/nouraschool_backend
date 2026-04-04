package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.etablissement.BatimentDto;
import com.nouraschool.domain.dtos.etablissement.SalleDto;
import com.nouraschool.domain.services.BatimentService;
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

@Path("/api/v1/batiments")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BatimentResource {

    @Inject
    BatimentService batimentService;

    @Inject
    SalleService salleService;

    @GET
    public List<BatimentDto> findAll() {
        return batimentService.findAll();
    }

    @GET
    @Path("/{id}")
    public BatimentDto findById(@PathParam("id") String id) {
        return batimentService.findById(UUID.fromString(id));
    }

    @GET
    @Path("/{id}/salles")
    public List<SalleDto> getSalles(@PathParam("id") String id) {
        return salleService.findAll(Optional.of(UUID.fromString(id)));
    }

    @POST
    public Response create(@Valid BatimentDto dto) {
        return Response.status(Response.Status.CREATED).entity(batimentService.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public BatimentDto update(@PathParam("id") String id, @Valid BatimentDto dto) {
        return batimentService.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        batimentService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
