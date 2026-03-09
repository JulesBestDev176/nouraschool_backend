package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "salle")
public class SalleEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "batiment_id", nullable = false)
    public UUID batimentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batiment_id", insertable = false, updatable = false)
    public BatimentEntity batiment;

    @Column(nullable = false, length = 50)
    public String nom;

    public Integer capacite;

    @Column(name = "type_salle", length = 50)
    public String typeSalle;

    @Column(nullable = false)
    public Boolean actif = true;
}
