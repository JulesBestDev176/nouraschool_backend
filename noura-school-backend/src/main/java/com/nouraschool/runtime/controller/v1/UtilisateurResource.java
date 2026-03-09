package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.AssignerCyclesRequest;
import com.nouraschool.domain.dtos.ReinitialiserMdpRequest;
import com.nouraschool.domain.dtos.UserCreateDto;
import com.nouraschool.domain.dtos.UserDto;
import com.nouraschool.domain.services.UtilisateurService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/utilisateurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN"})
@SecurityRequirement(name = "Bearer")
public class UtilisateurResource {

    @Inject
    UtilisateurService utilisateurService;

    @GET
    public List<UserDto> findAll() {
        return utilisateurService.findAll();
    }

    @GET
    @Path("/{id}")
    public UserDto findById(@PathParam("id") UUID id) {
        return utilisateurService.findById(id);
    }

    @POST
    public Response create(@Valid UserCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(utilisateurService.create(dto)).build();
    }

    @PATCH
    @Path("/{id}")
    public UserDto update(@PathParam("id") UUID id, @Valid UserDto dto) {
        return utilisateurService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        utilisateurService.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/reinitialiser-mdp")
    public Response reinitialiserMdp(@PathParam("id") UUID id, @Valid ReinitialiserMdpRequest request) {
        utilisateurService.reinitialiserMdp(id, request.getNouveauMotDePasse());
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/cycles")
    public Response assignerCycles(@PathParam("id") UUID id, @Valid AssignerCyclesRequest request) {
        utilisateurService.assignerCycles(id, request.getCycleIds());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/cycles/{cycleId}")
    public Response retirerCycle(@PathParam("id") UUID id, @PathParam("cycleId") UUID cycleId) {
        utilisateurService.retirerCycle(id, cycleId);
        return Response.noContent().build();
    }
}
