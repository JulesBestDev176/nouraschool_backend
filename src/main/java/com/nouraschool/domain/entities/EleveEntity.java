package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.Genre;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "eleves")
public class EleveEntity extends UserEntity {

    @Column(nullable = false, unique = true)
    public String matricule;

    @Column(name = "date_naissance", nullable = false)
    public LocalDate dateNaissance;

    @Column(name = "lieu_naissance")
    public String lieuNaissance;

    @Enumerated(EnumType.STRING)
    public Genre genre;

    @Column(name = "numero_urgence")
    public String numeroUrgence;

    @Column(name = "date_inscription")
    public LocalDate dateInscription;

    @Column(name = "photo_url")
    public String photoUrl;

    @ManyToOne
    @JoinColumn(name = "classe_id")
    public ClasseEntity classe;

    @ManyToMany
    @JoinTable(
            name = "eleve_parent",
            joinColumns = @JoinColumn(name = "eleve_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    public List<ParentEntity> parents;

    @OneToMany(mappedBy = "eleve")
    public List<NoteEntity> notes;

    @OneToMany(mappedBy = "eleve")
    public List<AbsenceEleveEntity> absences;

    @OneToMany(mappedBy = "eleve")
    public List<ReclamationEntity> reclamations;

    @OneToMany(mappedBy = "eleve")
    public List<PaiementEntity> paiements;
}