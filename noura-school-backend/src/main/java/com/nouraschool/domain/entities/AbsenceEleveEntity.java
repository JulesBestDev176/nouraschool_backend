package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.TypeAbsence;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "absences_eleves")
public class AbsenceEleveEntity extends AbstractUuidEntity {

    @ManyToOne
    @JoinColumn(name = "eleve_id", nullable = false)
    public EleveEntity eleve;

    @Column(nullable = false)
    public LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypeAbsence typeAbsence;

    @Column(nullable = false)
    public Boolean justifiee = false;

    @Column(columnDefinition = "TEXT")
    public String motif;

    @Column(name = "document_justificatif_url")
    public String documentJustificatifUrl;

    @Column(name = "declared_by")
    public UUID declaredBy;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}