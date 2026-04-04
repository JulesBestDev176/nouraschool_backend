package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "appel_ligne", uniqueConstraints = @UniqueConstraint(columnNames = {"appel_id", "eleve_id"}))
public class AppelLigneEntity extends AbstractEntity {

    @Column(name = "appel_id", nullable = false)
    public UUID appelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appel_id", insertable = false, updatable = false)
    public AppelEntity appel;

    @Column(name = "eleve_id", nullable = false)
    public UUID eleveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", insertable = false, updatable = false)
    public EleveEntity eleve;

    @Column(nullable = false, length = 20)
    public String statut; // PRESENT | ABSENT | RETARD
}
