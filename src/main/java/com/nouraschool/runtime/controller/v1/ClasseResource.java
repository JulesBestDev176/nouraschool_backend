package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.ClasseDto;
import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.usecases.admin.AdminClasseUseCase;
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

@Path("/api/v1/classes")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClasseResource {

    @Inject
    AdminClasseUseCase adminClasseUseCase;

    @GET
    public PageDto<ClasseDto> findAll(@QueryParam("page") @DefaultValue("0") int page,
                                      @QueryParam("size") @DefaultValue("50") int size) {
        List<ClasseDto> all = adminClasseUseCase.findAll();
        return toPage(all, page, size);
    }

    @GET
    @Path("/{id}")
    public ClasseDto findById(@PathParam("id") String id) {
        return adminClasseUseCase.findById(UUID.fromString(id));
    }

    @POST
    public Response create(@Valid ClasseDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminClasseUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public ClasseDto update(@PathParam("id") String id, @Valid ClasseDto dto) {
        return adminClasseUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminClasseUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }

    private PageDto<ClasseDto> toPage(List<ClasseDto> items, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int from = Math.min(safePage * safeSize, items.size());
        int to = Math.min(from + safeSize, items.size());
        List<ClasseDto> content = from >= to ? Collections.emptyList() : items.subList(from, to);
        return PageDto.of(content, safePage, safeSize, items.size());
    }
}
