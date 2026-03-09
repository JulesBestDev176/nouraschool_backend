package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "absence_personnel")
public class AbsencePersonnelEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "utilisateur_id", nullable = false)
    public UUID utilisateurId;

    @Column(name = "date_debut", nullable = false)
    public LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    public LocalDate dateFin;

    @Column(length = 255)
    public String motif;

    @Column(name = "type_absence", length = 50)
    public String typeAbsence;

    @Column(name = "justificatif_url", length = 500)
    public String justificatifUrl;

    @Column(nullable = false, length = 20)
    public String statut = "EN_ATTENTE";

    @Column(name = "valide_par")
    public UUID validePar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", insertable = false, updatable = false)
    public UserEntity utilisateur;
}
