package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "convocation")
public class ConvocationEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    public ParentEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    public EleveEntity eleve;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String motif;

    @Column(name = "date_convocation", nullable = false)
    public Instant dateConvocation;

    @Column(nullable = false, length = 20)
    public String statut = "EN_ATTENTE";

    @Column(name = "compte_rendu", columnDefinition = "TEXT")
    public String compteRendu;

    @Column(name = "cree_par", nullable = false)
    public UUID creePar;
}
