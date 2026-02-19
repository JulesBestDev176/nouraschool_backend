package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.TypeEvenement;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "calendrier_scolaire")
public class CalendrierScolaireEntity extends AbstractUuidEntity {

    @Column(name = "annee_scolaire", nullable = false)
    public String anneeScolaire;

    @Column(nullable = false)
    public String titre;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_evenement", nullable = false)
    public TypeEvenement typeEvenement;

    @Column(name = "date_debut", nullable = false)
    public LocalDate dateDebut;

    @Column(name = "date_fin")
    public LocalDate dateFin;

    @Column(name = "concerne_classes")
    public String concerneClasses;

    @Column(nullable = false)
    public Boolean publier = true;
}