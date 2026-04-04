package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cahier_texte")
public class CahierTexteEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "cours_id", nullable = false)
    public UUID coursId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", insertable = false, updatable = false)
    public CoursEntity cours;

    @Column(name = "date_cours", nullable = false)
    public LocalDate dateCours;

    @Column(name = "contenu_traite", columnDefinition = "TEXT")
    public String contenuTraite;

    @Column(columnDefinition = "TEXT")
    public String observations;

    @Column(name = "etape_programme")
    public String etapeProgramme;

    @Column(name = "programme_valide", nullable = false)
    public Boolean programmeValide = false;
}
