package com.nouraschool.domain.entities;

import com.nouraschool.domain.enums.ModePaiement;
import com.nouraschool.domain.enums.StatutPaiement;
import com.nouraschool.domain.enums.TypePaiement;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "paiements")
public class PaiementEntity extends AbstractEntity {

    @Column(name = "tenant_id")
    public UUID tenantId;

    @Column(name = "inscription_id")
    public UUID inscriptionId;

    @ManyToOne
    @JoinColumn(name = "eleve_id", nullable = false)
    public EleveEntity eleve;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    public ParentEntity parent;

    @Column(nullable = false, unique = true)
    public String reference;

    @Column(nullable = false)
    public Double montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypePaiement typePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false)
    public ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    @Column(name = "annee_scolaire", nullable = false)
    public String anneeScolaire;

    @Column(nullable = false)
    public String trimestre;

    @Column(name = "transaction_id")
    public String transactionId;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(name = "date_paiement")
    public LocalDateTime datePaiement;

    @Column(name = "valide_par")
    public UUID validePar;

}
