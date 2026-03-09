package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "matieres")
public class MatiereEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

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
