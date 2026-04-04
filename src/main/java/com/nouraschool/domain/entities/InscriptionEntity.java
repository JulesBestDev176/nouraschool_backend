package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "inscription")
public class InscriptionEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "numero_inscription", nullable = false, unique = true, length = 50)
    public String numeroInscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    public EleveEntity eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    public ClasseEntity classe;

    @Column(name = "annee_academique_id", nullable = false)
    public UUID anneeAcademiqueId;

    @Column(nullable = false, length = 20)
    public String statut = "ACTIF"; // ACTIF | TRANSFERE | EXCLU

    @Column(name = "cree_par", nullable = false)
    public UUID creePar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cree_par", insertable = false, updatable = false)
    public UserEntity createur;
}
