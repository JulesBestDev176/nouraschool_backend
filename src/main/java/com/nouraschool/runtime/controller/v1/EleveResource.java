package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.EleveCreateDto;
import com.nouraschool.domain.dtos.EleveDto;
import com.nouraschool.domain.dtos.EleveUpdateDto;
import com.nouraschool.domain.dtos.PageDto;
import com.nouraschool.domain.usecases.admin.AdminEleveUseCase;
import com.nouraschool.domain.utils.PageUtils;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.UUID;

@Path("/api/v1/eleves")
@RolesAllowed({"ADMIN", "CAISSIER"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EleveResource {

    @Inject
    AdminEleveUseCase adminEleveUseCase;

    @GET
    public PageDto<EleveDto> findAll(@QueryParam("page") @DefaultValue("0") int page,
                                     @QueryParam("size") @DefaultValue("20") int size,
                                     @QueryParam("sortBy") String sortBy,
                                     @QueryParam("asc") @DefaultValue("true") boolean asc) {
        return adminEleveUseCase.findAll(PageUtils.of(page, size, sortBy, asc));
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

    @PATCH
    @Path("/{id}")
    public EleveDto patch(@PathParam("id") String id, @Valid EleveUpdateDto dto) {
        return adminEleveUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminEleveUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
