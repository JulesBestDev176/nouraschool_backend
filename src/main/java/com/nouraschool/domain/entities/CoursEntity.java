package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cours = Matière + Professeur + Classe + Année académique.
 */
@Entity
@Table(name = "cours", uniqueConstraints = @UniqueConstraint(
        columnNames = {"matiere_id", "professeur_id", "classe_id", "annee_academique_id"}))
public class CoursEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "matiere_id", nullable = false)
    public UUID matiereId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", insertable = false, updatable = false)
    public MatiereEntity matiere;

    @Column(name = "professeur_id", nullable = false)
    public UUID professeurId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id", insertable = false, updatable = false)
    public UserEntity professeur;

    @Column(name = "classe_id", nullable = false)
    public UUID classeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", insertable = false, updatable = false)
    public ClasseEntity classe;

    @Column(name = "annee_academique_id", nullable = false)
    public UUID anneeAcademiqueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", insertable = false, updatable = false)
    public AnneeAcademiqueEntity anneeAcademique;

    @Column(name = "volume_horaire_hebdo", precision = 4, scale = 1)
    public BigDecimal volumeHoraireHebdo;

    @Column(precision = 4, scale = 2)
    public BigDecimal coefficient;
}
