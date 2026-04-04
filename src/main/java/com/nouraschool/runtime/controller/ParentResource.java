package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.*;
import com.nouraschool.domain.usecases.parent.ParentUseCase;
import com.nouraschool.runtime.security.CurrentUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Path("/api/parent")
@RolesAllowed("PARENT")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParentResource {

    @Inject
    ParentUseCase parentUseCase;

    @GET
    @Path("/profil")
    public ParentDto monProfil(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.monProfil(id);
    }

    @GET
    @Path("/enfants")
    public List<EleveDto> mesEnfants(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.mesEnfants(id);
    }

    @GET
    @Path("/enfants/{eleveId}/profil")
    public EleveDto profilEnfant(@Context SecurityContext sc, @PathParam("eleveId") UUID eleveId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.profilEnfant(id, eleveId);
    }

    @GET
    @Path("/enfants/{eleveId}/notes")
    public List<NoteDto> notesEnfant(@Context SecurityContext sc, @PathParam("eleveId") UUID eleveId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.notesEnfant(id, eleveId);
    }

    @GET
    @Path("/enfants/{eleveId}/bulletins")
    public List<BulletinDto> bulletinsEnfant(@Context SecurityContext sc, @PathParam("eleveId") UUID eleveId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.bulletinsEnfant(id, eleveId);
    }

    @GET
    @Path("/enfants/{eleveId}/absences")
    public List<AbsenceEleveDto> absencesEnfant(@Context SecurityContext sc, @PathParam("eleveId") UUID eleveId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.absencesEnfant(id, eleveId);
    }

    @GET
    @Path("/enfants/{eleveId}/emploi-du-temps")
    public List<EmploiDuTempsDto> emploiDuTempsEnfant(@Context SecurityContext sc, @PathParam("eleveId") UUID eleveId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.emploiDuTempsEnfant(id, eleveId);
    }

    @GET
    @Path("/paiements")
    public List<PaiementDto> historiquePaiements(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.historiquePaiements(id);
    }

    @GET
    @Path("/notifications")
    public List<NotificationDto> mesNotifications(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return parentUseCase.mesNotifications(id);
    }
}
