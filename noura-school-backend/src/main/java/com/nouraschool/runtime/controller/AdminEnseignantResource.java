package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.EnseignantCreateDto;
import com.nouraschool.domain.dtos.EnseignantDto;
import com.nouraschool.domain.usecases.admin.AdminEnseignantUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/enseignants")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminEnseignantResource {

    @Inject
    AdminEnseignantUseCase adminEnseignantUseCase;

    @GET
    public List<EnseignantDto> findAll() {
        return adminEnseignantUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public EnseignantDto findById(@PathParam("id") String id) {
        return adminEnseignantUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid EnseignantCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminEnseignantUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public EnseignantDto update(@PathParam("id") String id, @Valid EnseignantDto dto) {
        return adminEnseignantUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminEnseignantUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
