package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.StatutReclamation;
import com.nouraschool.domain.enums.TypeReclamation;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reclamations")
public class ReclamationEntity extends AbstractUuidEntity {

    @ManyToOne
    @JoinColumn(name = "eleve_id", nullable = false)
    public EleveEntity eleve;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypeReclamation typeReclamation;

    @Column(nullable = false)
    public String objet;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;

    @ManyToOne
    @JoinColumn(name = "note_id")
    public NoteEntity note;

    @ManyToOne
    @JoinColumn(name = "absence_id")
    public AbsenceEleveEntity absence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public StatutReclamation statut = StatutReclamation.EN_ATTENTE;

    @Column(columnDefinition = "TEXT")
    public String reponse;

    @Column(name = "traite_par")
    public UUID traitePar;

    @Column(name = "date_traitement")
    public LocalDateTime dateTraitement;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
