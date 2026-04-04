package com.nouraschool.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "caissiers")
public class CaissierEntity extends UserEntity {

    @Column(name = "numero_caisse")
    public String numeroCaisse;

    @Column(name = "code_caissier")
    public String codeCaissier;
}
