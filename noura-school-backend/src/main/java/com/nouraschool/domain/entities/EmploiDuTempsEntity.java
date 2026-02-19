package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.JourSemaine;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "emplois_du_temps")
public class EmploiDuTempsEntity extends AbstractUuidEntity {

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    public ClasseEntity classe;

    @ManyToOne
    @JoinColumn(name = "matiere_id", nullable = false)
    public MatiereEntity matiere;

    @ManyToOne
    @JoinColumn(name = "enseignant_id", nullable = false)
    public EnseignantEntity enseignantEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine", nullable = false)
    public JourSemaine jourSemaine;

    @Column(name = "heure_debut", nullable = false)
    public LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    public LocalTime heureFin;

    @Column(nullable = false)
    public String salle;

    @Column(name = "annee_scolaire", nullable = false)
    public String anneeScolaire;

    @Column(name = "date_debut_validite")
    public LocalDate dateDebutValidite;

    @Column(name = "date_fin_validite")
    public LocalDate dateFinValidite;
}
