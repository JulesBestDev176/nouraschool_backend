package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.TypeEvaluation;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class NoteEntity extends AbstractUuidEntity {

    @ManyToOne
    @JoinColumn(name = "eleve_id", nullable = false)
    public EleveEntity eleve;

    @ManyToOne
    @JoinColumn(name = "matiere_id", nullable = false)
    public MatiereEntity matiere;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypeEvaluation typeEvaluation;

    @Column(nullable = false)
    public Double note;

    @Column(name = "note_sur", nullable = false)
    public Double noteSur = 20.0;

    @Column(nullable = false)
    public String trimestre;

    @Column(name = "annee_scolaire", nullable = false)
    public String anneeScolaire;

    @Column(name = "date_evaluation")
    public LocalDate dateEvaluation;

    @Column(columnDefinition = "TEXT")
    public String commentaire;

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