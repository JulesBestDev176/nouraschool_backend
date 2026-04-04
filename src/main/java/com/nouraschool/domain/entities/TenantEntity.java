package com.nouraschool.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant")
public class TenantEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(nullable = false, unique = true, length = 100)
    public String slug;

    @Column(nullable = false)
    public String nom;

    @Column(name = "email_contact", length = 254)
    public String emailContact;

    @Column(length = 20)
    public String telephone;

    @Column(columnDefinition = "TEXT")
    public String adresse;

    @Column(name = "logo_url", length = 500)
    public String logoUrl;

    @Column(nullable = false, length = 50)
    public String plan = "TRIAL";

    @Column(nullable = false)
    public Boolean actif = true;

    @Column(name = "date_expiration")
    public java.time.LocalDate dateExpiration;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    public Instant updatedAt;

    @Column(name = "deleted_at")
    public Instant deletedAt;
}
