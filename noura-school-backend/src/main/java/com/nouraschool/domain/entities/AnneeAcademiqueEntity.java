package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "annee_academique", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "libelle"}))
public class AnneeAcademiqueEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    public TenantEntity tenant;

    @Column(nullable = false, length = 20)
    public String libelle;

    @Column(name = "date_debut", nullable = false)
    public LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    public LocalDate dateFin;

    @Column(name = "est_courante", nullable = false)
    public Boolean estCourante = false;

    @Column(nullable = false)
    public Boolean actif = true;
}
