package com.nouraschool.runtime.controller;

import com.nouraschool.domain.dtos.PaiementDto;
import com.nouraschool.domain.enums.StatutPaiement;
import com.nouraschool.domain.usecases.caisse.CaisseUseCase;
import com.nouraschool.runtime.security.CurrentUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Path("/api/caisse")
@RolesAllowed("CAISSIER")
@SecurityRequirement(name = "Bearer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CaisseResource {

    @Inject
    CaisseUseCase caisseUseCase;

    @GET
    @Path("/paiements")
    public List<PaiementDto> listerPaiements() {
        return caisseUseCase.listerPaiements();
    }

    @GET
    @Path("/paiements/statut/{statut}")
    public List<PaiementDto> listerParStatut(@PathParam("statut") StatutPaiement statut) {
        return caisseUseCase.listerPaiementsParStatut(statut);
    }

    @GET
    @Path("/paiements/historique")
    public List<PaiementDto> historique(
            @QueryParam("debut") String debut,
            @QueryParam("fin") String fin) {
        var d = debut != null ? LocalDateTime.parse(debut) : LocalDateTime.now().minusMonths(1);
        var f = fin != null ? LocalDateTime.parse(fin) : LocalDateTime.now();
        return caisseUseCase.historiquePaiements(d, f);
    }

    @GET
    @Path("/paiements/{id}")
    public PaiementDto consulter(@PathParam("id") String id) {
        return caisseUseCase.consulterPaiement(UUID.fromString(id));
    }

    @POST
    @Path("/paiements/{id}/valider")
    public PaiementDto valider(@Context SecurityContext sc, @PathParam("id") String id) {
        var userId = CurrentUser.getUserId(sc).orElse(null);
        return caisseUseCase.validerPaiement(UUID.fromString(id), userId);
    }
}
