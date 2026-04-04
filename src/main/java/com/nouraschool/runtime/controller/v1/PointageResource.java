package com.nouraschool.runtime.controller.v1;

import com.nouraschool.domain.dtos.rh.PointageCreateDto;
import com.nouraschool.domain.dtos.rh.PointageDto;
import com.nouraschool.domain.dtos.rh.PointageRapportDto;
import com.nouraschool.domain.services.PointageService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Path("/api/v1/pointages")
@RolesAllowed({"ADMIN", "RH"})
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PointageResource {

    @Inject
    PointageService pointageService;

    @POST
    public Response create(@Valid PointageCreateDto dto) {
        return Response.status(Response.Status.CREATED).entity(pointageService.create(dto)).build();
    }

    @GET
    public List<PointageDto> findAll() {
        return pointageService.findAll();
    }

    @GET
    @Path("/rapport")
    public PointageRapportDto getRapport(@QueryParam("debut") String debutStr, @QueryParam("fin") String finStr) {
        LocalDate debut = LocalDate.parse(debutStr);
        LocalDate fin = LocalDate.parse(finStr);
        Instant debutInstant = debut.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant finInstant = fin.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().minusSeconds(1);
        return pointageService.getRapport(debutInstant, finInstant);
    }
}
