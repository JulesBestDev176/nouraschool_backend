package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.etablissement.CoursCreateDto;
import com.nouraschool.domain.dtos.etablissement.CoursDto;
import com.nouraschool.domain.services.CoursService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/api/v1/cours")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoursResource {

    @Inject
    CoursService coursService;

    @GET
    public List<CoursDto> findAll(@QueryParam("classeId") String classeId) {
        Optional<UUID> classeUuid = Optional.ofNullable(classeId)
                .filter(s -> !s.isBlank())
                .map(UUID::fromString);
        return coursService.findAll(classeUuid);
    }

    @GET
    @Path("/{id}")
    public CoursDto findById(@PathParam("id") String id) {
        return coursService.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid CoursCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(coursService.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public CoursDto update(@PathParam("id") String id, @Valid CoursDto dto) {
        return coursService.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        coursService.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
