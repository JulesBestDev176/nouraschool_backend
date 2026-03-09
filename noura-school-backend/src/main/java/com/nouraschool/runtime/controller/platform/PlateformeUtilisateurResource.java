package com.nouraschool.runtime.controller.platform;

import com.nouraschool.domain.dtos.platform.PlateformeUtilisateurCreateDto;
import com.nouraschool.domain.dtos.platform.PlateformeUtilisateurDto;
import com.nouraschool.domain.services.PlateformeUtilisateurService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/platform/utilisateurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "SUPER_ADMIN", "GESTIONNAIRE"})
@SecurityRequirement(name = "Bearer")
public class PlateformeUtilisateurResource {

    @Inject
    PlateformeUtilisateurService service;

    @GET
    public List<PlateformeUtilisateurDto> list() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        return service.findById(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response create(@Valid PlateformeUtilisateurCreateDto dto) {
        PlateformeUtilisateurDto created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}/actif")
    public Response setActif(@PathParam("id") UUID id, @QueryParam("actif") boolean actif) {
        service.setActif(id, actif);
        return Response.noContent().build();
    }
}
