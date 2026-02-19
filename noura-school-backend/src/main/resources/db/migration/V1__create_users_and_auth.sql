-- Users table (base for JOINED inheritance)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
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

-- User subclasses (JOINED inheritance)
CREATE TABLE administrateurs (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    niveau_acces VARCHAR(255),
    droits_speciaux VARCHAR(255)
);

CREATE TABLE caissiers (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    numero_caisse VARCHAR(255),
    code_caissier VARCHAR(255)
);

CREATE TABLE enseignants (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    matricule VARCHAR(255) NOT NULL UNIQUE,
    specialite VARCHAR(255) NOT NULL,
    date_embauche DATE,
    numero_cnps VARCHAR(255),
    numero_securite_sociale VARCHAR(255)
);

CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    niveau VARCHAR(255) NOT NULL,
    annee_scolaire VARCHAR(255) NOT NULL,
    effectif_max INTEGER,
    salle_classe VARCHAR(255)
);

CREATE TABLE eleves (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    matricule VARCHAR(255) NOT NULL UNIQUE,
    date_naissance DATE NOT NULL,
    lieu_naissance VARCHAR(255),
    genre VARCHAR(50),
    numero_urgence VARCHAR(255),
    date_inscription DATE,
    photo_url VARCHAR(255),
    classe_id BIGINT REFERENCES classes(id)
);

CREATE TABLE parents (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    profession VARCHAR(255) NOT NULL,
    lieu_travail VARCHAR(255),
    telephone_travail VARCHAR(255),
    lien_parente VARCHAR(50)
);

-- Join table for eleve-parent many-to-many
CREATE TABLE eleve_parent (
    eleve_id BIGINT NOT NULL REFERENCES eleves(id) ON DELETE CASCADE,
    parent_id BIGINT NOT NULL REFERENCES parents(id) ON DELETE CASCADE,
    PRIMARY KEY (eleve_id, parent_id)
);

-- Refresh tokens for JWT authentication
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
