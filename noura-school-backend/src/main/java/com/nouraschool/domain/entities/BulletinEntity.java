package com.nouraschool.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bulletins")
public class BulletinEntity extends AbstractUuidEntity {

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

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "fichier_pdf_url")
    public String fichierPdfUrl;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
