package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.LienParente;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "parents")
public class ParentEntity extends UserEntity {

    @Column(nullable = false)
    public String profession;

    @Column(name = "lieu_travail")
    public String lieuTravail;

    @Column(name = "telephone_travail")
    public String telephoneTravail;

    @Enumerated(EnumType.STRING)
    @Column(name = "lien_parente")
    public LienParente lienParente;

    @ManyToMany(mappedBy = "parents")
    public List<EleveEntity> enfants;

    @OneToMany(mappedBy = "parent")
    public List<PaiementEntity> paiements;
}
