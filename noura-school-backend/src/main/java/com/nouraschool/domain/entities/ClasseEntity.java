package com.nouraschool.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "classes")
public class ClasseEntity extends AbstractEntity {

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

    @OneToMany(mappedBy = "classe")
    public List<EleveEntity> eleves;

    @OneToMany(mappedBy = "classe")
    public List<MatiereClasseEntity> matiereClasses;

    @OneToMany(mappedBy = "classe")
    public List<EmploiDuTempsEntity> emploisDuTemps;
}
