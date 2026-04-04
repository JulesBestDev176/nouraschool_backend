package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.EnseignantCreateDto;
import com.nouraschool.domain.dtos.EnseignantDto;
import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.usecases.admin.AdminEnseignantUseCase;
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

@Path("/api/v1/enseignants")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnseignantResource {

    @Inject
    AdminEnseignantUseCase adminEnseignantUseCase;

    @GET
    public PageDto<EnseignantDto> findAll(@QueryParam("page") @DefaultValue("0") int page,
                                          @QueryParam("size") @DefaultValue("20") int size) {
        List<EnseignantDto> all = adminEnseignantUseCase.findAll();
        return toPage(all, page, size);
    }

    @GET
    @Path("/{id}")
    public EnseignantDto findById(@PathParam("id") String id) {
        return adminEnseignantUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid EnseignantCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminEnseignantUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public EnseignantDto update(@PathParam("id") String id, @Valid EnseignantDto dto) {
        return adminEnseignantUseCase.update(UUID.fromString(id), dto);
    }

    @PATCH
    @Path("/{id}")
    public EnseignantDto patch(@PathParam("id") String id, @Valid EnseignantDto dto) {
        return adminEnseignantUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminEnseignantUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }

    private PageDto<EnseignantDto> toPage(List<EnseignantDto> items, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int from = Math.min(safePage * safeSize, items.size());
        int to = Math.min(from + safeSize, items.size());
        List<EnseignantDto> content = from >= to ? Collections.emptyList() : items.subList(from, to);
        return PageDto.of(content, safePage, safeSize, items.size());
    }
}
