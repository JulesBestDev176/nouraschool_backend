package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.NoteDto;
import com.nouraschool.domain.usecases.admin.AdminNoteUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/notes")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminNoteResource {

    @Inject
    AdminNoteUseCase adminNoteUseCase;

    @GET
    public List<NoteDto> findAll() {
        return adminNoteUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public NoteDto findById(@PathParam("id") String id) {
        return adminNoteUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid NoteDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminNoteUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public NoteDto update(@PathParam("id") String id, @Valid NoteDto dto) {
        return adminNoteUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminNoteUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
