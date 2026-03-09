package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.etablissement.EtablissementStatsDto;
import com.nouraschool.domain.services.EtablissementStatsService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

@Path("/api/v1/stats/etablissement")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
@SecurityRequirement(name = "Bearer")
public class EtablissementStatsResource {

    @Inject
    EtablissementStatsService etablissementStatsService;

    @GET
    public EtablissementStatsDto getStats() {
        return etablissementStatsService.getStats();
    }
}
