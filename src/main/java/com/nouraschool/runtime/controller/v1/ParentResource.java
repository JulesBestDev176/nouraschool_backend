package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.dtos.ParentCreateDto;
import com.nouraschool.domain.dtos.ParentDto;
import com.nouraschool.domain.usecases.admin.AdminParentUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/parents")
@RolesAllowed({"ADMIN", "CAISSIER"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParentResource {

    @Inject
    AdminParentUseCase adminParentUseCase;

    @GET
    public PageDto<ParentDto> findAll(@QueryParam("page") @DefaultValue("0") int page,
                                      @QueryParam("size") @DefaultValue("20") int size) {
        List<ParentDto> all = adminParentUseCase.findAll();
        return toPage(all, page, size);
    }

    @GET
    @Path("/{id}")
    public ParentDto findById(@PathParam("id") String id) {
        return adminParentUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid ParentCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminParentUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public ParentDto update(@PathParam("id") String id, @Valid ParentDto dto) {
        return adminParentUseCase.update(UUID.fromString(id), dto);
    }

    @PATCH
    @Path("/{id}")
    public ParentDto patch(@PathParam("id") String id, @Valid ParentDto dto) {
        return adminParentUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminParentUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }

    private PageDto<ParentDto> toPage(List<ParentDto> items, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int from = Math.min(safePage * safeSize, items.size());
        int to = Math.min(from + safeSize, items.size());
        List<ParentDto> content = from >= to ? Collections.emptyList() : items.subList(from, to);
        return PageDto.of(content, safePage, safeSize, items.size());
    }
}
