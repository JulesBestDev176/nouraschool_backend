package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.rh.AbsencePersonnelCreateDto;
import com.nouraschool.domain.dtos.rh.AbsencePersonnelDto;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.services.AbsencePersonnelService;
import com.nouraschool.runtime.security.CurrentUser;
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

@Path("/api/v1/absences-personnel")
@RolesAllowed({"ADMIN", "RH"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AbsencePersonnelResource {

    @Inject
    AbsencePersonnelService absencePersonnelService;

    @POST
    public Response create(@Valid AbsencePersonnelCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(absencePersonnelService.create(dto)).build();
    }

    @GET
    public List<AbsencePersonnelDto> findAll() {
        return absencePersonnelService.findAll();
    }

    @GET
    @Path("/{id}")
    public AbsencePersonnelDto findById(@PathParam("id") String id) {
        return absencePersonnelService.findById(UUID.fromString(id));
    }

    @PATCH
    @Path("/{id}/valider")
    public AbsencePersonnelDto valider(@PathParam("id") String id, @Context SecurityContext securityContext) {
        UUID validatedBy = CurrentUser.getUserId(securityContext)
                .orElseThrow(() -> new InvalidRequestException("Utilisateur non authentifié"));
        return absencePersonnelService.valider(UUID.fromString(id), validatedBy);
    }

    @PATCH
    @Path("/{id}/refuser")
    public AbsencePersonnelDto refuser(@PathParam("id") String id, @Context SecurityContext securityContext) {
        UUID validatedBy = CurrentUser.getUserId(securityContext)
                .orElseThrow(() -> new InvalidRequestException("Utilisateur non authentifié"));
        return absencePersonnelService.refuser(UUID.fromString(id), validatedBy);
    }
}
