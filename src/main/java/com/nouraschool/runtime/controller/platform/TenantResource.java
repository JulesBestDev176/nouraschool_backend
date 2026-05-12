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
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * API plateforme : gestion des tenants (établissements).
 * Nécessite un rôle plateforme. Un administrateur d'école ne doit pas gérer les tenants.
 */
@Path("/api/platform/tenants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"SUPER_ADMIN", "GESTIONNAIRE"})
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

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/png", "image/jpeg", "image/webp", "image/svg+xml"
    );
    private static final long MAX_LOGO_SIZE = 2L * 1024 * 1024; // 2 Mo

    @POST
    @Path("/{id}/logo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({"SUPER_ADMIN", "GESTIONNAIRE"})
    public Response uploadLogo(@PathParam("id") UUID id, @RestForm("file") FileUpload file) {
        if (file == null || file.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Fichier requis")).build();
        }
        String contentType = file.contentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Format non supporté. Utilisez PNG, JPEG, WebP ou SVG.")).build();
        }
        if (file.size() > MAX_LOGO_SIZE) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Fichier trop volumineux. Maximum 2 Mo.")).build();
        }
        String extension = switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/svg+xml" -> "svg";
            default -> "jpg";
        };
        try (InputStream stream = Files.newInputStream(file.filePath())) {
            String logoUrl = tenantService.uploadLogo(id, stream, contentType, file.size(), extension);
            return Response.ok(Map.of("logoUrl", logoUrl)).build();
        } catch (IOException e) {
            return Response.serverError()
                    .entity(Map.of("error", "Erreur lors de l'upload.")).build();
        }
    }
}
