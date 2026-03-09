package com.nouraschool.runtime.controller.platform;

import com.nouraschool.domain.dtos.platform.PlateformeStatsDto;
import com.nouraschool.domain.services.PlateformeStatsService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

@Path("/api/platform/stats")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "SUPER_ADMIN", "GESTIONNAIRE"})
@SecurityRequirement(name = "Bearer")
public class PlateformeStatsResource {

    @Inject
    PlateformeStatsService service;

    @GET
    public PlateformeStatsDto getStats() {
        return service.getStats();
    }
}
