package com.nouraschool.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Annonce (tableau d'affichage visible par profs/élèves).
 */
@Entity
@Table(name = "annonce")
public class AnnonceEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    public TenantEntity tenant;

    @Column(nullable = false, length = 255)
    public String titre;

    @Column(columnDefinition = "TEXT")
    public String contenu;

    @Column(name = "date_debut")
    public LocalDate dateDebut;

    @Column(name = "date_fin")
    public LocalDate dateFin;

    @Column(nullable = false)
    public Boolean actif = true;
}
