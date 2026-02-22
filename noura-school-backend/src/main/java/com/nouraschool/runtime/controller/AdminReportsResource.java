package com.nouraschool.runtime.controller;

import com.nouraschool.domain.services.BulletinExportService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.io.ByteArrayOutputStream;

@Path("/api/admin/reports")
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
@Produces({"application/json", "application/pdf", "application/zip"})
@Consumes("application/json")
public class AdminReportsResource {

    @Inject
    BulletinExportService bulletinExportService;

    /** Rapport de fin de trimestre (statistiques par classe: effectif, moyennes). */
    @GET
    @Path("/rapport-trimestre")
    @Produces("application/pdf")
    public Response rapportTrimestre(@QueryParam("anneeScolaire") String anneeScolaire,
                                     @QueryParam("trimestre") String trimestre) {
        if (anneeScolaire == null || trimestre == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("anneeScolaire et trimestre requis").build();
        }
        ByteArrayOutputStream pdf = bulletinExportService.generateRapportTrimestrePdf(anneeScolaire, trimestre);
        String filename = "rapport_trimestre_" + trimestre.replace(" ", "_") + "_" + anneeScolaire.replace("-", "_") + ".pdf";
        StreamingOutput stream = output -> output.write(pdf.toByteArray());
        return Response.ok(stream)
                .type("application/pdf")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
    }
}
