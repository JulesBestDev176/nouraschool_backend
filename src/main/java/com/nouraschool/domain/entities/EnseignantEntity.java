package com.nouraschool.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "enseignants")
public class EnseignantEntity extends UserEntity {

    @Column(nullable = false, unique = true)
    public String matricule;

    @Column(nullable = false)
    public String specialite;

    @Column(name = "date_embauche")
    public LocalDate dateEmbauche;

    @Column(name = "numero_cnps")
    public String numeroCNPS;

    @Column(name = "numero_securite_sociale")
    public String numeroSecuriteSociale;

    @OneToMany(mappedBy = "enseignantEntity")
    public List<MatiereClasseEntity> matiereClasses;

    @OneToMany(mappedBy = "enseignantEntity")
    public List<AbsenceEnseignantEntity> absences;
}