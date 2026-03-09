package com.nouraschool.runtime.controller.platform;

import com.nouraschool.domain.dtos.platform.TenantCreateDto;
import com.nouraschool.domain.dtos.platform.TenantDto;
import com.nouraschool.domain.dtos.platform.TenantUpdateDto;
import com.nouraschool.domain.services.TenantService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

/**
 * API plateforme : gestion des tenants (établissements).
 * Nécessite rôle ADMIN, SUPER_ADMIN ou GESTIONNAIRE.
 */
@Path("/api/platform/tenants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "SUPER_ADMIN", "GESTIONNAIRE"})
@SecurityRequirement(name = "Bearer")
public class TenantResource {

    @Inject
    TenantService tenantService;

    @GET
    public List<TenantDto> list() {
        return tenantService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        return tenantService.findById(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response create(@Valid TenantCreateDto dto) {
        TenantDto created = tenantService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public TenantDto update(@PathParam("id") UUID id, @Valid TenantUpdateDto dto) {
        return tenantService.update(id, dto);
    }

    @POST
    @Path("/{id}/suspend")
    @RolesAllowed({"SUPER_ADMIN"})
    public Response suspend(@PathParam("id") UUID id) {
        tenantService.suspend(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/reactivate")
    @RolesAllowed({"SUPER_ADMIN"})
    public Response reactivate(@PathParam("id") UUID id) {
        tenantService.reactivate(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"SUPER_ADMIN"})
    public Response delete(@PathParam("id") UUID id) {
        tenantService.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/auto-register")
    public Response autoRegister(@QueryParam("nom") String nom, @QueryParam("slug") String slug) {
        TenantDto dto = tenantService.autoRegister(nom, slug);
        return Response.status(Response.Status.CREATED).entity(dto).build();
    }
}
