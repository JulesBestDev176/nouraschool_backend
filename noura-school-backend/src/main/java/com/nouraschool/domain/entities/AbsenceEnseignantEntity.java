package com.nouraschool.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "absences_enseignants")
public class AbsenceEnseignantEntity extends AbstractEntity {

    @Column(name = "date_debut", nullable = false)
    public LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    public LocalDate dateFin;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String motif;

    @Column(nullable = false)
    public Boolean justifiee = false;

    @Column(name = "document_justificatif_url")
    public String documentJustificatifUrl;

    @Column(name = "remplacant_id")
    public UUID remplacantId;

    @Column(name = "notification_envoyee")
    public Boolean notificationEnvoyee = false;

    @ManyToOne
    @JoinColumn(name = "enseignant_id", nullable = false)
    public EnseignantEntity enseignantEntity;

}

