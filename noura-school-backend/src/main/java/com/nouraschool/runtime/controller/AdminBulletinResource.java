package com.nouraschool.runtime.controller;

import com.nouraschool.domain.enums.FormatExport;
import com.nouraschool.domain.dtos.BulletinDto;
import com.nouraschool.domain.utils.PageUtils;
import com.nouraschool.domain.usecases.admin.AdminBulletinUseCase;
import com.nouraschool.domain.services.BulletinExportService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

@Path("/api/admin/bulletins")
@RolesAllowed({"ADMIN", "SURVEILLANT"})
@SecurityRequirement(name = "Bearer")
@Produces({MediaType.APPLICATION_JSON, "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
@Consumes(MediaType.APPLICATION_JSON)
public class AdminBulletinResource {

    @Inject
    AdminBulletinUseCase adminBulletinUseCase;
    @Inject
    BulletinExportService bulletinExportService;

    @GET
    public Object findAll(@QueryParam("page") @DefaultValue("-1") int page,
                         @QueryParam("size") @DefaultValue("20") int size,
                         @QueryParam("sortBy") String sortBy,
                         @QueryParam("asc") @DefaultValue("true") boolean asc) {
        if (page >= 0) {
            return adminBulletinUseCase.findAll(PageUtils.of(page, size, sortBy, asc));
        }
        return adminBulletinUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public BulletinDto findById(@PathParam("id") String id) {
        return adminBulletinUseCase.findById(parseUuidOr400(id));
    }

    @GET
    @Path("/{id}/download")
    @Produces({"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    public Response download(@PathParam("id") String id, @QueryParam("format") @DefaultValue("pdf") String format) {
        BulletinDto bulletin = adminBulletinUseCase.findById(parseUuidOr400(id));
        FormatExport formatExport = switch (format.toLowerCase()) {
            case "word", "docx" -> FormatExport.WORD;
            case "excel", "xlsx" -> FormatExport.EXCEL;
            default -> FormatExport.PDF;
        };
        ByteArrayOutputStream bytes = bulletinExportService.export(bulletin, formatExport);
        String mediaType = switch (formatExport) {
            case PDF -> "application/pdf";
            case WORD -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        };
        String extension = switch (formatExport) {
            case PDF -> "pdf";
            case WORD -> "docx";
            case EXCEL -> "xlsx";
        };
        String filename = "bulletin_" + id + "_" + bulletin.getTrimestre() + "." + extension;
        StreamingOutput stream = output -> output.write(bytes.toByteArray());
        return Response.ok(stream)
                .type(mediaType)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
    }

    /** Export des bulletins d'une classe (ZIP de PDF). */
    @GET
    @Path("/download/by-classe")
    @Produces("application/zip")
    public Response downloadByClasse(@QueryParam("classeId") UUID classeId,
                                     @QueryParam("anneeScolaire") String anneeScolaire,
                                     @QueryParam("trimestre") String trimestre) {
        if (classeId == null || anneeScolaire == null || trimestre == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("classeId, anneeScolaire et trimestre requis").build();
        }
        ByteArrayOutputStream zip = bulletinExportService.exportByClassePdf(classeId, anneeScolaire, trimestre);
        String filename = "bulletins_classe_" + trimestre.replace(" ", "_") + "_" + anneeScolaire.replace("-", "_") + ".zip";
        StreamingOutput stream = output -> output.write(zip.toByteArray());
        return Response.ok(stream)
                .type("application/zip")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
    }

    /** Export de tous les bulletins de l'école pour un trimestre (ZIP de PDF). */
    @GET
    @Path("/download/all")
    @Produces("application/zip")
    public Response downloadAll(@QueryParam("anneeScolaire") String anneeScolaire,
                                @QueryParam("trimestre") String trimestre) {
        if (anneeScolaire == null || trimestre == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("anneeScolaire et trimestre requis").build();
        }
        ByteArrayOutputStream zip = bulletinExportService.exportAllBulletinsPdf(anneeScolaire, trimestre);
        String filename = "bulletins_ecole_" + trimestre.replace(" ", "_") + "_" + anneeScolaire.replace("-", "_") + ".zip";
        StreamingOutput stream = output -> output.write(zip.toByteArray());
        return Response.ok(stream)
                .type("application/zip")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
    }

    @POST
    public Response create(@Valid BulletinDto dto) {
        return Response.status(Response.Status.CREATED).entity(adminBulletinUseCase.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public BulletinDto update(@PathParam("id") String id, @Valid BulletinDto dto) {
        return adminBulletinUseCase.update(parseUuidOr400(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminBulletinUseCase.delete(parseUuidOr400(id));
        return Response.noContent().build();
    }

    static UUID parseUuidOr400(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid bulletin id: " + id).build());
        }
    }
}
