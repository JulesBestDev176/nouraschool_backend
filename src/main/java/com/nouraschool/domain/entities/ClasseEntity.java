package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "classes")
public class ClasseEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(nullable = false, unique = true)
    public String nom;

    @Column(nullable = false)
    public String niveau;

    @Column(name = "annee_scolaire", nullable = false)
    public String anneeScolaire;

    @Column(name = "effectif_max")
    public Integer effectifMax;

    @Column(name = "salle_classe")
    public String salleClasse;

    @Column(name = "niveau_id")
    public UUID niveauId;

    @Column(name = "annee_academique_id")
    public UUID anneeAcademiqueId;

    @Column(name = "salle_id")
    public UUID salleId;

    @OneToMany(mappedBy = "classe")
    public List<EleveEntity> eleves;

    @OneToMany(mappedBy = "classe")
    public List<MatiereClasseEntity> matiereClasses;

    @OneToMany(mappedBy = "classe")
    public List<EmploiDuTempsEntity> emploisDuTemps;
}
