package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "bulletins")
public class BulletinEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "eleve_id", nullable = false)
    public EleveEntity eleve;

    @Column(nullable = false)
    public String trimestre;

    @Column(name = "annee_scolaire", nullable = false)
    public String anneeScolaire;

    @Column(nullable = false)
    public Double moyenne;

    @Column(name = "moyenne_classe")
    public Double moyenneClasse;

    @Column(nullable = false)
    public Integer rang;

    @Column(name = "total_eleves")
    public Integer totalEleves;

    @Column(columnDefinition = "TEXT")
    public String appreciation;

    @Column(name = "nombre_absences")
    public Integer nombreAbsences = 0;

    @Column(name = "nombre_retards")
    public Integer nombreRetards = 0;

    @Column(name = "fichier_pdf_url")
    public String fichierPdfUrl;

    @Column(length = 20)
    public String statut = "BROUILLON";

    @Column(name = "soumis_par")
    public UUID soumisPar;

    @Column(name = "valide_par")
    public UUID validePar;
}
