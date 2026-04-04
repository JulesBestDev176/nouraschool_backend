-- Drop all tables (CASCADE to remove FKs). Order: child tables first, then parent.
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS calendrier_scolaire CASCADE;
DROP TABLE IF EXISTS paiements CASCADE;
DROP TABLE IF EXISTS reclamations CASCADE;
DROP TABLE IF EXISTS bulletins CASCADE;
DROP TABLE IF EXISTS absences_enseignants CASCADE;
DROP TABLE IF EXISTS absences_eleves CASCADE;
DROP TABLE IF EXISTS notes CASCADE;
DROP TABLE IF EXISTS emplois_du_temps CASCADE;
DROP TABLE IF EXISTS matiere_classe CASCADE;
DROP TABLE IF EXISTS eleve_parent CASCADE;
DROP TABLE IF EXISTS eleves CASCADE;
DROP TABLE IF EXISTS parents CASCADE;
DROP TABLE IF EXISTS enseignants CASCADE;
DROP TABLE IF EXISTS caissiers CASCADE;
DROP TABLE IF EXISTS administrateurs CASCADE;
DROP TABLE IF EXISTS classes CASCADE;
DROP TABLE IF EXISTS matieres CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop sequences from V3 (no longer used)
DROP SEQUENCE IF EXISTS users_seq;
DROP SEQUENCE IF EXISTS classes_seq;
DROP SEQUENCE IF EXISTS refresh_tokens_seq;
DROP SEQUENCE IF EXISTS matieres_seq;
DROP SEQUENCE IF EXISTS matiere_classe_seq;
DROP SEQUENCE IF EXISTS emplois_du_temps_seq;
DROP SEQUENCE IF EXISTS notes_seq;
DROP SEQUENCE IF EXISTS absences_eleves_seq;
DROP SEQUENCE IF EXISTS absences_enseignants_seq;
DROP SEQUENCE IF EXISTS bulletins_seq;
DROP SEQUENCE IF EXISTS reclamations_seq;
DROP SEQUENCE IF EXISTS paiements_seq;
DROP SEQUENCE IF EXISTS notifications_seq;
DROP SEQUENCE IF EXISTS calendrier_scolaire_seq;

-- Recreate schema with UUID primary keys (gen_random_uuid() for default)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    telephone VARCHAR(255) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE administrateurs (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    niveau_acces VARCHAR(255),
    droits_speciaux VARCHAR(255)
);

CREATE TABLE caissiers (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    numero_caisse VARCHAR(255),
    code_caissier VARCHAR(255)
);

CREATE TABLE enseignants (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    matricule VARCHAR(255) NOT NULL UNIQUE,
    specialite VARCHAR(255) NOT NULL,
    date_embauche DATE,
    numero_cnps VARCHAR(255),
    numero_securite_sociale VARCHAR(255)
);

CREATE TABLE classes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(255) NOT NULL UNIQUE,
    niveau VARCHAR(255) NOT NULL,
    annee_scolaire VARCHAR(255) NOT NULL,
    effectif_max INTEGER,
    salle_classe VARCHAR(255)
);

CREATE TABLE eleves (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    matricule VARCHAR(255) NOT NULL UNIQUE,
    date_naissance DATE NOT NULL,
    lieu_naissance VARCHAR(255),
    genre VARCHAR(50),
    numero_urgence VARCHAR(255),
    date_inscription DATE,
    photo_url VARCHAR(255),
    classe_id UUID REFERENCES classes(id)
);

CREATE TABLE parents (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    profession VARCHAR(255) NOT NULL,
    lieu_travail VARCHAR(255),
    telephone_travail VARCHAR(255),
    lien_parente VARCHAR(50)
);

CREATE TABLE eleve_parent (
    eleve_id UUID NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    parent_id UUID NOT NULL REFERENCES parents(id) ON DELETE CASCADE,
    PRIMARY KEY (eleve_id, parent_id)
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

CREATE TABLE matieres (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    coefficient INTEGER NOT NULL,
    categorie VARCHAR(255) NOT NULL
);

CREATE TABLE matiere_classe (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    matiere_id UUID NOT NULL REFERENCES matieres(id) ON DELETE CASCADE,
    classe_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    enseignant_id UUID NOT NULL REFERENCES enseignants(id) ON DELETE CASCADE,
    annee_scolaire VARCHAR(255) NOT NULL,
    volume_horaire INTEGER
);

CREATE TABLE emplois_du_temps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    classe_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    matiere_id UUID NOT NULL REFERENCES matieres(id) ON DELETE CASCADE,
    enseignant_id UUID NOT NULL REFERENCES enseignants(id) ON DELETE CASCADE,
    jour_semaine VARCHAR(50) NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    salle VARCHAR(255) NOT NULL,
    annee_scolaire VARCHAR(255) NOT NULL,
    date_debut_validite DATE,
    date_fin_validite DATE
);

CREATE TABLE notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    matiere_id UUID NOT NULL REFERENCES matieres(id) ON DELETE CASCADE,
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

CREATE TABLE absences_eleves (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    type_absence VARCHAR(50) NOT NULL,
    justifiee BOOLEAN NOT NULL DEFAULT FALSE,
    motif TEXT,
    document_justificatif_url VARCHAR(255),
    declared_by UUID,
    created_at TIMESTAMP
);

CREATE TABLE absences_enseignants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enseignant_id UUID NOT NULL REFERENCES enseignants(id) ON DELETE CASCADE,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    motif TEXT NOT NULL,
    justifiee BOOLEAN NOT NULL DEFAULT FALSE,
    document_justificatif_url VARCHAR(255),
    remplacant_id UUID,
    notification_envoyee BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP
);

CREATE TABLE bulletins (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
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

CREATE TABLE reclamations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    type_reclamation VARCHAR(50) NOT NULL,
    objet VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    note_id UUID REFERENCES notes(id) ON DELETE SET NULL,
    absence_id UUID REFERENCES absences_eleves(id) ON DELETE SET NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    reponse TEXT,
    traite_par UUID,
    date_traitement TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE paiements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    parent_id UUID REFERENCES parents(id) ON DELETE SET NULL,
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
    valide_par UUID,
    created_at TIMESTAMP
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    titre VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    lue BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id UUID,
    reference_type VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE calendrier_scolaire (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    annee_scolaire VARCHAR(255) NOT NULL,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type_evenement VARCHAR(50) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    concerne_classes VARCHAR(255),
    publier BOOLEAN NOT NULL DEFAULT TRUE
);
