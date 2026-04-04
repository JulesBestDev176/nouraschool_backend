package com.nouraschool.domain.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "matiere_classe")
public class MatiereClasseEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @ManyToOne
    @JoinColumn(name = "matiere_id", nullable = false)
    public MatiereEntity matiere;

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    public ClasseEntity classe;

    @ManyToOne
    @JoinColumn(name = "enseignant_id", nullable = false)
    public EnseignantEntity enseignantEntity;

    @Column(name = "annee_scolaire", nullable = false)
    public String anneeScolaire;

    @Column(name = "volume_horaire")
    public Integer volumeHoraire;
}
