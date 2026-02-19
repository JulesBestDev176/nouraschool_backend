package com.nouraschool.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "matieres")
public class MatiereEntity extends AbstractUuidEntity {

    @Column(nullable = false, unique = true)
    public String nom;

    @Column(nullable = false, unique = true)
    public String code;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(nullable = false)
    public Integer coefficient;

    @Column(nullable = false)
    public String categorie;

    @OneToMany(mappedBy = "matiere")
    public List<MatiereClasseEntity> matiereClasses;

    @OneToMany(mappedBy = "matiere")
    public List<NoteEntity> notes;
}
