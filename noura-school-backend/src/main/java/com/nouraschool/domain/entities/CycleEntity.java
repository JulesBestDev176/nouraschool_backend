package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Cycle scolaire : COLLEGE, LYCEE.
 */
@Entity
@Table(name = "cycle", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "code"}))
public class CycleEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    public TenantEntity tenant;

    @Column(nullable = false, length = 20)
    public String code;

    @Column(nullable = false, length = 100)
    public String libelle;

    @Column(nullable = false)
    public Boolean actif = true;
}
