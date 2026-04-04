package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.MatiereClasseDto;
import com.nouraschool.domain.usecases.admin.AdminMatiereClasseUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/matieres-classes")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminMatiereClasseResource {

    @Inject
    AdminMatiereClasseUseCase adminMatiereClasseUseCase;

    @GET
    public List<MatiereClasseDto> findAll() {
        return adminMatiereClasseUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public MatiereClasseDto findById(@PathParam("id") String id) {
        return adminMatiereClasseUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid MatiereClasseDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminMatiereClasseUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public MatiereClasseDto update(@PathParam("id") String id, @Valid MatiereClasseDto dto) {
        return adminMatiereClasseUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminMatiereClasseUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
