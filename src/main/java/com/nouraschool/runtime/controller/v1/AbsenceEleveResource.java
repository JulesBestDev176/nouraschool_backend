package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.AbsenceEleveDto;
import com.nouraschool.domain.usecases.admin.AdminAbsenceEleveUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/absences-eleves")
@RolesAllowed({"ADMIN", "SURVEILLANT"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AbsenceEleveResource {

    @Inject
    AdminAbsenceEleveUseCase adminAbsenceEleveUseCase;

    @GET
    public List<AbsenceEleveDto> findAll(@QueryParam("eleveId") String eleveId) {
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

    @POST
    @Path("/{id}/approuver")
    public AbsenceEleveDto approuver(@PathParam("id") String id, @Context SecurityContext securityContext) {
        UUID approuvePar = com.nouraschool.runtime.security.CurrentUser.getUserId(securityContext)
                .orElseThrow(() -> new com.nouraschool.domain.exception.errors.InvalidRequestException("Utilisateur non authentifié"));
        return adminAbsenceEleveUseCase.approuver(UUID.fromString(id), approuvePar);
    }

    @POST
    @Path("/{id}/rejeter")
    public AbsenceEleveDto rejeter(@PathParam("id") String id, @Context SecurityContext securityContext) {
        UUID approuvePar = com.nouraschool.runtime.security.CurrentUser.getUserId(securityContext)
                .orElseThrow(() -> new com.nouraschool.domain.exception.errors.InvalidRequestException("Utilisateur non authentifié"));
        return adminAbsenceEleveUseCase.rejeter(UUID.fromString(id), approuvePar);
    }
}
