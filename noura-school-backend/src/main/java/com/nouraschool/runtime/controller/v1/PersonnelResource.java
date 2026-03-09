package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.rh.PersonnelCreateDto;
import com.nouraschool.domain.dtos.rh.PersonnelDto;
import com.nouraschool.domain.services.PersonnelService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/v1/personnel")
@RolesAllowed({"ADMIN", "RH"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonnelResource {

    @Inject
    PersonnelService personnelService;

    @POST
    public Response create(@Valid PersonnelCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(personnelService.create(dto)).build();
    }

    @GET
    public List<PersonnelDto> findAll() {
        return personnelService.findAll();
    }

    @GET
    @Path("/{id}")
    public PersonnelDto findById(@PathParam("id") String id) {
        return personnelService.findById(UUID.fromString(id));
    }

    @PATCH
    @Path("/{id}")
    public PersonnelDto update(@PathParam("id") String id, @Valid PersonnelCreateDto dto) {
        return personnelService.update(UUID.fromString(id), dto);
    }
}
