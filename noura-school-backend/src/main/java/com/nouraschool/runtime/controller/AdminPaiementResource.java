package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.PaiementDto;
import com.nouraschool.domain.usecases.admin.AdminPaiementUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/paiements")
@RolesAllowed({"ADMIN", "CAISSIER"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminPaiementResource {

    @Inject
    AdminPaiementUseCase adminPaiementUseCase;

    @GET
    public List<PaiementDto> findAll() {
        return adminPaiementUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public PaiementDto findById(@PathParam("id") String id) {
        return adminPaiementUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid PaiementDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminPaiementUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public PaiementDto update(@PathParam("id") String id, @Valid PaiementDto dto) {
        return adminPaiementUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminPaiementUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
