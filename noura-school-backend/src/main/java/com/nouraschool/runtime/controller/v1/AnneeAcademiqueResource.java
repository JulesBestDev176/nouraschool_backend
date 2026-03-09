package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.etablissement.AnneeAcademiqueDto;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.services.AnneeAcademiqueService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/annees-academiques")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnneeAcademiqueResource {

    @Inject
    AnneeAcademiqueService anneeAcademiqueService;

    @GET
    public List<AnneeAcademiqueDto> findAll() {
        return anneeAcademiqueService.findAll();
    }

    @GET
    @Path("/courante")
    public AnneeAcademiqueDto findCourante() {
        return anneeAcademiqueService.findCourante()
                .orElseThrow(() -> new NotFoundException("Annee academique courante introuvable"));
    }

    @GET
    @Path("/{id}")
    public AnneeAcademiqueDto findById(@PathParam("id") String id) {
        return anneeAcademiqueService.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid AnneeAcademiqueDto dto) {
        return Response.status(Response.Status.CREATED).entity(anneeAcademiqueService.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public AnneeAcademiqueDto update(@PathParam("id") String id, @Valid AnneeAcademiqueDto dto) {
        return anneeAcademiqueService.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        anneeAcademiqueService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/activer")
    public AnneeAcademiqueDto activer(@PathParam("id") String id) {
        return anneeAcademiqueService.activer(UUID.fromString(id));
    }
}
