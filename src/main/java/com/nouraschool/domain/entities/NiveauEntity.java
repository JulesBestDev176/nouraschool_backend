package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "niveau", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "cycle_id", "code"}))
public class NiveauEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "cycle_id", nullable = false)
    public UUID cycleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", insertable = false, updatable = false)
    public CycleEntity cycle;

    @Column(nullable = false, length = 20)
    public String code;

    @Column(nullable = false, length = 100)
    public String libelle;

    @Column(nullable = false)
    public Integer ordre;

    @Column(nullable = false)
    public Boolean actif = true;
}
