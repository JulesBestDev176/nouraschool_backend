package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.CalendrierScolaireDto;
import com.nouraschool.domain.usecases.admin.AdminCalendrierScolaireUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/calendrier-scolaire")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminCalendrierScolaireResource {

    @Inject
    AdminCalendrierScolaireUseCase adminCalendrierScolaireUseCase;

    @GET
    public List<CalendrierScolaireDto> findAll() {
        return adminCalendrierScolaireUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public CalendrierScolaireDto findById(@PathParam("id") String id) {
        return adminCalendrierScolaireUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid CalendrierScolaireDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminCalendrierScolaireUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public CalendrierScolaireDto update(@PathParam("id") String id, @Valid CalendrierScolaireDto dto) {
        return adminCalendrierScolaireUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminCalendrierScolaireUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
