package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.AbsenceEnseignantDto;
import com.nouraschool.domain.dtos.AppelDto;
import com.nouraschool.domain.dtos.CahierTexteDto;
import com.nouraschool.domain.dtos.BulletinDto;
import com.nouraschool.domain.dtos.EnseignantDto;
import com.nouraschool.domain.dtos.MatiereClasseDto;
import com.nouraschool.domain.dtos.EmploiDuTempsDto;
import com.nouraschool.domain.dtos.NoteDto;
import com.nouraschool.domain.dtos.ReclamationDto;
import com.nouraschool.domain.usecases.enseignant.EnseignantUseCase;
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
import java.util.UUID;

@Path("/api/enseignant")
@RolesAllowed("ENSEIGNANT")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnseignantResource {

    @Inject
    EnseignantUseCase enseignantUseCase;

    @GET
    @Path("/profil")
    public EnseignantDto monProfil(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.monProfil(id);
    }

    @GET
    @Path("/classes-matieres")
    public List<MatiereClasseDto> mesClassesEtMatieres(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.mesClassesEtMatieres(id);
    }

    @GET
    @Path("/emploi-du-temps")
    public List<EmploiDuTempsDto> monEmploiDuTemps(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.monEmploiDuTemps(id);
    }

    @GET
    @Path("/notes")
    public List<NoteDto> notesPourEleveMatiere(@Context SecurityContext sc,
            @QueryParam("eleveId") UUID eleveId, @QueryParam("matiereId") UUID matiereId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.notesPourEleveMatiere(id, eleveId, matiereId);
    }

    @POST
    @Path("/notes")
    public Response saisirNote(@Context SecurityContext sc, @Valid NoteDto dto) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return Response.status(Response.Status.CREATED).entity(enseignantUseCase.saisirNote(id, dto)).build();
    }

    @PUT
    @Path("/notes/{noteId}")
    public NoteDto modifierNote(@Context SecurityContext sc, @PathParam("noteId") UUID noteId, @Valid NoteDto dto) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.modifierNote(id, noteId, dto);
    }

    @GET
    @Path("/bulletins")
    public List<BulletinDto> consulterBulletins(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.consulterBulletins(id);
    }

    @POST
    @Path("/absences")
    public Response declarerAbsence(@Context SecurityContext sc, @Valid AbsenceEnseignantDto dto) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return Response.status(Response.Status.CREATED).entity(enseignantUseCase.declarerAbsence(id, dto)).build();
    }

    @GET
    @Path("/reclamations")
    public List<ReclamationDto> reclamationsMesMatieres(@Context SecurityContext sc) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.reclamationsMesMatieres(id);
    }

    @POST
    @Path("/appels")
    public Response creerAppel(@Context SecurityContext sc, @Valid AppelDto dto) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return Response.status(Response.Status.CREATED).entity(enseignantUseCase.creerAppel(id, dto)).build();
    }

    @GET
    @Path("/appels")
    public List<AppelDto> listeAppels(@Context SecurityContext sc, @QueryParam("coursId") UUID coursId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.listeAppels(id, coursId);
    }

    @PATCH
    @Path("/appels/{appelId}/soumettre")
    public AppelDto soumettreAppel(@Context SecurityContext sc, @PathParam("appelId") UUID appelId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.soumettreAppel(id, appelId);
    }

    @POST
    @Path("/cahier-texte")
    public Response creerCahierTexte(@Context SecurityContext sc, @Valid CahierTexteDto dto) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return Response.status(Response.Status.CREATED).entity(enseignantUseCase.creerCahierTexte(id, dto)).build();
    }

    @GET
    @Path("/cahier-texte")
    public List<CahierTexteDto> listeCahierTexte(@Context SecurityContext sc, @QueryParam("coursId") UUID coursId) {
        var id = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.listeCahierTexte(id, coursId);
    }

    @PATCH
    @Path("/cahier-texte/{id}")
    public CahierTexteDto modifierCahierTexte(@Context SecurityContext sc, @PathParam("id") UUID id, @Valid CahierTexteDto dto) {
        var userId = CurrentUser.getUserId(sc).orElseThrow();
        return enseignantUseCase.modifierCahierTexte(userId, id, dto);
    }
}
