package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appel")
public class AppelEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "cours_id", nullable = false)
    public UUID coursId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", insertable = false, updatable = false)
    public CoursEntity cours;

    @Column(name = "date_cours", nullable = false)
    public LocalDate dateCours;

    @Column(name = "heure_debut")
    public LocalTime heureDebut;

    @Column(nullable = false, length = 20)
    public String statut = "BROUILLON";

    @Column(name = "soumis_par", nullable = false)
    public UUID soumisPar;

    @OneToMany(mappedBy = "appel", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<AppelLigneEntity> lignes;
}
