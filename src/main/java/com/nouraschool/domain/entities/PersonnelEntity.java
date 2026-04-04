package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "personnel")
public class PersonnelEntity extends AbstractEntity {

    @Column(name = "tenant_id", nullable = false)
    public UUID tenantId;

    @Column(name = "utilisateur_id", nullable = false)
    public UUID utilisateurId;

    @Column(name = "numero_matricule")
    public String numeroMatricule;

    @Column(name = "type_contrat")
    public String typeContrat;

    @Column(name = "date_embauche")
    public LocalDate dateEmbauche;

    public BigDecimal salaire;

    @Column(name = "solde_conge", nullable = false)
    public Integer soldeConge = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", insertable = false, updatable = false)
    public UserEntity utilisateur;
}
