package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.MatiereDto;
import com.nouraschool.domain.usecases.admin.AdminMatiereUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/matieres")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminMatiereResource {

    @Inject
    AdminMatiereUseCase adminMatiereUseCase;

    @GET
    public List<MatiereDto> findAll() {
        return adminMatiereUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public MatiereDto findById(@PathParam("id") String id) {
        return adminMatiereUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid MatiereDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminMatiereUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public MatiereDto update(@PathParam("id") String id, @Valid MatiereDto dto) {
        return adminMatiereUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminMatiereUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
