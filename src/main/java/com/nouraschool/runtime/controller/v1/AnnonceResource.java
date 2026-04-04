package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.etablissement.AnnonceCreateDto;
import com.nouraschool.domain.dtos.etablissement.AnnonceDto;
import com.nouraschool.domain.services.AnnonceService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/annonces")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnonceResource {

    @Inject
    AnnonceService annonceService;

    @GET
    @RolesAllowed({"ADMIN", "ENSEIGNANT", "ELEVE"})
    public List<AnnonceDto> findAll() {
        return annonceService.findAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "ENSEIGNANT", "ELEVE"})
    public AnnonceDto findById(@PathParam("id") String id) {
        return annonceService.findById(UUID.fromString(id));
    }

    @POST
    @RolesAllowed("ADMIN")
    public Response create(@Valid AnnonceCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(annonceService.create(dto)).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public AnnonceDto update(@PathParam("id") String id, @Valid AnnonceCreateDto dto) {
        return annonceService.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam("id") String id) {
        annonceService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
