package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pointage")
public class PointageEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "utilisateur_id", nullable = false)
    public UUID utilisateurId;

    @Column(name = "type_pointage", nullable = false, length = 10)
    public String typePointage;

    @Column(name = "date_heure", nullable = false)
    public Instant dateHeure;

    public String methode;

    @Column(name = "created_by")
    public UUID createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", insertable = false, updatable = false)
    public UserEntity utilisateur;
}
