-- Matieres
CREATE TABLE matieres (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    coefficient INTEGER NOT NULL,
    categorie VARCHAR(255) NOT NULL
);

-- Matiere-Classe (many-to-many with enseignant)
CREATE TABLE matiere_classe (
    id BIGSERIAL PRIMARY KEY,
    matiere_id BIGINT NOT NULL REFERENCES matieres(id) ON DELETE CASCADE,
    classe_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    enseignant_id BIGINT NOT NULL REFERENCES enseignants(id) ON DELETE CASCADE,
    annee_scolaire VARCHAR(255) NOT NULL,
    volume_horaire INTEGER
);

-- Emplois du temps
CREATE TABLE emplois_du_temps (
    id BIGSERIAL PRIMARY KEY,
    classe_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    matiere_id BIGINT NOT NULL REFERENCES matieres(id) ON DELETE CASCADE,
    enseignant_id BIGINT NOT NULL REFERENCES enseignants(id) ON DELETE CASCADE,
    jour_semaine VARCHAR(50) NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    salle VARCHAR(255) NOT NULL,
    annee_scolaire VARCHAR(255) NOT NULL,
    date_debut_validite DATE,
    date_fin_validite DATE
);

-- Notes
CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    eleve_id BIGINT NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    matiere_id BIGINT NOT NULL REFERENCES matieres(id) ON DELETE CASCADE,
    type_evaluation VARCHAR(50) NOT NULL,
    note DOUBLE PRECISION NOT NULL,
    note_sur DOUBLE PRECISION NOT NULL DEFAULT 20.0,
    trimestre VARCHAR(255) NOT NULL,
    annee_scolaire VARCHAR(255) NOT NULL,
    date_evaluation DATE,
    commentaire TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Absences eleves
CREATE TABLE absences_eleves (
    id BIGSERIAL PRIMARY KEY,
    eleve_id BIGINT NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    type_absence VARCHAR(50) NOT NULL,
    justifiee BOOLEAN NOT NULL DEFAULT FALSE,
    motif TEXT,
    document_justificatif_url VARCHAR(255),
    declared_by BIGINT,
    created_at TIMESTAMP
);

-- Absences enseignants
CREATE TABLE absences_enseignants (
    id BIGSERIAL PRIMARY KEY,
    enseignant_id BIGINT NOT NULL REFERENCES enseignants(id) ON DELETE CASCADE,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    motif TEXT NOT NULL,
    justifiee BOOLEAN NOT NULL DEFAULT FALSE,
    document_justificatif_url VARCHAR(255),
    remplacant_id BIGINT,
    notification_envoyee BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP
);

-- Bulletins
CREATE TABLE bulletins (
    id BIGSERIAL PRIMARY KEY,
    eleve_id BIGINT NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    trimestre VARCHAR(255) NOT NULL,
    annee_scolaire VARCHAR(255) NOT NULL,
    moyenne DOUBLE PRECISION NOT NULL,
    moyenne_classe DOUBLE PRECISION,
    rang INTEGER NOT NULL,
    total_eleves INTEGER,
    appreciation TEXT,
    nombre_absences INTEGER DEFAULT 0,
    nombre_retards INTEGER DEFAULT 0,
    created_at TIMESTAMP,
    fichier_pdf_url VARCHAR(255)
);

-- Reclamations
CREATE TABLE reclamations (
    id BIGSERIAL PRIMARY KEY,
    eleve_id BIGINT NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    type_reclamation VARCHAR(50) NOT NULL,
    objet VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    note_id BIGINT REFERENCES notes(id) ON DELETE SET NULL,
    absence_id BIGINT REFERENCES absences_eleves(id) ON DELETE SET NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    reponse TEXT,
    traite_par BIGINT,
    date_traitement TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Paiements
CREATE TABLE paiements (
    id BIGSERIAL PRIMARY KEY,
    eleve_id BIGINT NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    parent_id BIGINT REFERENCES parents(id) ON DELETE SET NULL,
    reference VARCHAR(255) NOT NULL UNIQUE,
    montant DOUBLE PRECISION NOT NULL,
    type_paiement VARCHAR(50) NOT NULL,
    mode_paiement VARCHAR(50) NOT NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    annee_scolaire VARCHAR(255) NOT NULL,
    trimestre VARCHAR(255) NOT NULL,
    transaction_id VARCHAR(255),
    description TEXT,
    date_paiement TIMESTAMP,
    valide_par BIGINT,
    created_at TIMESTAMP
);

-- Notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    lue BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id BIGINT,
    reference_type VARCHAR(255),
    created_at TIMESTAMP
);

-- Calendrier scolaire
CREATE TABLE calendrier_scolaire (
    id BIGSERIAL PRIMARY KEY,
    annee_scolaire VARCHAR(255) NOT NULL,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type_evenement VARCHAR(50) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    concerne_classes VARCHAR(255),
    publier BOOLEAN NOT NULL DEFAULT TRUE
);
