package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.ParentCreateDto;
import com.nouraschool.domain.dtos.ParentDto;
import com.nouraschool.domain.usecases.admin.AdminParentUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/parents")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminParentResource {

    @Inject
    AdminParentUseCase adminParentUseCase;

    @GET
    public List<ParentDto> findAll() {
        return adminParentUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public ParentDto findById(@PathParam("id") String id) {
        return adminParentUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid ParentCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminParentUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public ParentDto update(@PathParam("id") String id, @Valid ParentDto dto) {
        return adminParentUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminParentUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
