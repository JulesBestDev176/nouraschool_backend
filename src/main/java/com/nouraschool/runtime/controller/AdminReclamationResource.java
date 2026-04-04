package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.ReclamationDto;
import com.nouraschool.domain.usecases.admin.AdminReclamationUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/reclamations")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminReclamationResource {

    @Inject
    AdminReclamationUseCase adminReclamationUseCase;

    @GET
    public List<ReclamationDto> findAll() {
        return adminReclamationUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public ReclamationDto findById(@PathParam("id") String id) {
        return adminReclamationUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid ReclamationDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminReclamationUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public ReclamationDto update(@PathParam("id") String id, @Valid ReclamationDto dto) {
        return adminReclamationUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminReclamationUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
