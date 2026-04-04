package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.UserCreateDto;
import com.nouraschool.domain.dtos.UserDto;
import com.nouraschool.domain.usecases.admin.AdminUserUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/users")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminUserResource {

    @Inject
    AdminUserUseCase adminUserUseCase;

    @GET
    public List<UserDto> findAll() {
        return adminUserUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public UserDto findById(@PathParam("id") String id) {
        return adminUserUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid UserCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminUserUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public UserDto update(@PathParam("id") String id, @Valid UserDto dto) {
        return adminUserUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminUserUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
