package com.nouraschool.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrateurs")
public class AdministrateurEntity extends UserEntity {

    @Column(name = "niveau_acces")
    public String niveauAcces;

    @Column(name = "droits_speciaux")
    public String droitsSpeciaux;
}
