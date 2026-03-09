package com.nouraschool.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "batiment")
public class BatimentEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    public TenantEntity tenant;

    @Column(nullable = false, length = 100)
    public String nom;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(nullable = false)
    public Boolean actif = true;
}
