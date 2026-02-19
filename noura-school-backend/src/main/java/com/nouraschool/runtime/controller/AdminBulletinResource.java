package com.nouraschool.runtime.controller;

import com.nouraschool.domain.enums.FormatExport;
import com.nouraschool.domain.dtos.BulletinDto;
import com.nouraschool.domain.dtos.PageRequest;
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

import java.util.UUID;

@Path("/api/admin/bulletins")
@RolesAllowed("ADMIN")
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
            var pr = PageRequest.builder().page(page).size(size).sortBy(sortBy).ascending(asc).build();
            return adminBulletinUseCase.findAll(pr);
        }
        return adminBulletinUseCase.findAll();
    }

    @GET
    @Path("/{id}")
    public BulletinDto findById(@PathParam("id") String id) {
        return adminBulletinUseCase.findById(UUID.fromString(id));
    }

    @GET
    @Path("/{id}/download")
    @Produces({"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    public Response download(@PathParam("id") String id, @QueryParam("format") @DefaultValue("pdf") String format) {
        var bulletin = adminBulletinUseCase.findById(UUID.fromString(id));
        var formatExport = switch (format.toLowerCase()) {
            case "word", "docx" -> FormatExport.WORD;
            case "excel", "xlsx" -> FormatExport.EXCEL;
            default -> FormatExport.PDF;
        };
        var bytes = bulletinExportService.export(bulletin, formatExport);
        var mediaType = switch (formatExport) {
            case PDF -> "application/pdf";
            case WORD -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        };
        var extension = switch (formatExport) {
            case PDF -> "pdf";
            case WORD -> "docx";
            case EXCEL -> "xlsx";
        };
        var filename = "bulletin_" + id + "_" + bulletin.getTrimestre() + "." + extension;
        var stream = (StreamingOutput) output -> output.write(bytes.toByteArray());
        return Response.ok(stream)
                .type(mediaType)
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
        return adminBulletinUseCase.update(UUID.fromString(id), dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        adminBulletinUseCase.delete(UUID.fromString(id));
        return Response.noContent().build();
    }
}
