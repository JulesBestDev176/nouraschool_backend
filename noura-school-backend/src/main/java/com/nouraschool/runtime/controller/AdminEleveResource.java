package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.EleveCreateDto;
import com.nouraschool.domain.dtos.EleveDto;
import com.nouraschool.domain.dtos.EleveUpdateDto;
import com.nouraschool.domain.dtos.PageRequest;
import com.nouraschool.domain.usecases.admin.AdminEleveUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/admin/eleves")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminEleveResource {

    @Inject
    AdminEleveUseCase adminEleveUseCase;

    @GET
    public Object findAll(@QueryParam("page") @DefaultValue("-1") int page,
                         @QueryParam("size") @DefaultValue("20") int size,
                         @QueryParam("sortBy") String sortBy,
                         @QueryParam("asc") @DefaultValue("true") boolean asc) {
        if (page >= 0) {
            var pr = PageRequest.builder().page(page).size(size).sortBy(sortBy).ascending(asc).build();
            return adminEleveUseCase.findAll(pr);
        }
        return adminEleveUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public EleveDto findById(@PathParam("id") String id) {
        return adminEleveUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid EleveCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminEleveUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public EleveDto update(@PathParam("id") String id, @Valid EleveUpdateDto dto) {
        return adminEleveUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminEleveUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
