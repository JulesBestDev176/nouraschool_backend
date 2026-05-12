package com.nouraschool.domain.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Compte plateforme (super admin, gestionnaires Noura School) — task.md section 4.1.
 * Table plateforme_utilisateur, rôle SUPER_ADMIN | GESTIONNAIRE.
 */
@Entity
@Table(name = "plateforme_utilisateur")
public class PlateformeUtilisateurEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(nullable = false, length = 100)
    public String nom;

    @Column(nullable = false, length = 100)
    public String prenom;

    @Column(nullable = false, unique = true, length = 254)
    public String email;

    @Column(length = 20)
    public String telephone;

    @Column(name = "mot_de_passe", nullable = false)
    public String motDePasse;

    @Column(name = "role_plateforme", nullable = false, length = 50)
    public String rolePlateforme;

    @Column(nullable = false)
    public Boolean actif = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    public Instant updatedAt;

    @Column(name = "deleted_at")
    public Instant deletedAt;
}
