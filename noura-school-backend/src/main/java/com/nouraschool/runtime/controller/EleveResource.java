package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.*;
import com.nouraschool.domain.usecases.eleve.EleveUseCase;
import com.nouraschool.runtime.security.CurrentUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;

@Path("/api/eleve")
@RolesAllowed("ELEVE")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EleveResource {

    @Inject
    EleveUseCase eleveUseCase;

    @GET
    @Path("/profil")
    public EleveDto monProfil(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return eleveUseCase.monProfil(id);
    }

    @GET
    @Path("/notes")
    public List<NoteDto> mesNotes(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return eleveUseCase.mesNotes(id);
    }

    @GET
    @Path("/bulletins")
    public List<BulletinDto> mesBulletins(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return eleveUseCase.mesBulletins(id);
    }

    @GET
    @Path("/emploi-du-temps")
    public List<EmploiDuTempsDto> monEmploiDuTemps(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return eleveUseCase.monEmploiDuTemps(id);
    }

    @GET
    @Path("/absences")
    public List<AbsenceEleveDto> mesAbsences(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return eleveUseCase.mesAbsences(id);
    }

    @POST
    @Path("/reclamations")
    public Response soumettreReclamation(@Context SecurityContext sc, @Valid ReclamationDto dto) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return Response.status(Response.Status.CREATED).entity(eleveUseCase.soumettreReclamation(id, dto)).build();
    }

    @GET
    @Path("/reclamations")
    public List<ReclamationDto> mesReclamations(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return eleveUseCase.mesReclamations(id);
    }

    @GET
    @Path("/notifications")
    public List<NotificationDto> mesNotifications(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return eleveUseCase.mesNotifications(id);
    }

    @PATCH
    @Path("/notifications/{id}/lire")
    public NotificationDto marquerNotificationCommeLue(@Context SecurityContext sc, @PathParam("id") java.util.UUID id) {
        var userId = CurrentUser.getUserId(sc).orElseThrow();
        return eleveUseCase.marquerNotificationCommeLue(userId, id);
    }
}
