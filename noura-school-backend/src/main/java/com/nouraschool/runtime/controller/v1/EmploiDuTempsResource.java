package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.EmploiDuTempsDto;
import com.nouraschool.domain.usecases.admin.AdminEmploiDuTempsUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/emplois-du-temps")
@RolesAllowed({"ADMIN", "SURVEILLANT"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmploiDuTempsResource {

    @Inject
    AdminEmploiDuTempsUseCase adminEmploiDuTempsUseCase;

    @GET
    public List<EmploiDuTempsDto> findAll(@QueryParam("classeId") String classeId) {
        if (classeId != null && !classeId.isBlank()) {
            return adminEmploiDuTempsUseCase.findByClasseId(UUID.fromString(classeId));
        }
        return adminEmploiDuTempsUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public EmploiDuTempsDto findById(@PathParam("id") String id) {
        return adminEmploiDuTempsUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid EmploiDuTempsDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminEmploiDuTempsUseCase.create(dto)).build();
    }

    @PATCH
    @Path("/{id}")
    public EmploiDuTempsDto update(@PathParam("id") String id, @Valid EmploiDuTempsDto dto) {
        return adminEmploiDuTempsUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminEmploiDuTempsUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
