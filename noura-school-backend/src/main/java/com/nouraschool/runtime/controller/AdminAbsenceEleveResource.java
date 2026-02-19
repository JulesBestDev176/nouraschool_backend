package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.AbsenceEleveDto;
import com.nouraschool.domain.usecases.admin.AdminAbsenceEleveUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/absences-eleves")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminAbsenceEleveResource {

    @Inject
    AdminAbsenceEleveUseCase adminAbsenceEleveUseCase;

    @GET
    public List<AbsenceEleveDto> findAll() {
        return adminAbsenceEleveUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public AbsenceEleveDto findById(@PathParam("id") String id) {
        return adminAbsenceEleveUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid AbsenceEleveDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminAbsenceEleveUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public AbsenceEleveDto update(@PathParam("id") String id, @Valid AbsenceEleveDto dto) {
        return adminAbsenceEleveUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminAbsenceEleveUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
