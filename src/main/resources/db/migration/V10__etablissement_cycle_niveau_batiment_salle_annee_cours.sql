-- V10 : Tables établissement (task.md section 4.2)
-- cycle, niveau, batiment, salle, annee_academique, cours
-- + tenant_id sur classes, matieres, matiere_classe, emplois_du_temps

-- Cycle scolaire (COLLEGE, LYCEE)
CREATE TABLE cycle (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    code            VARCHAR(20) NOT NULL,
    libelle         VARCHAR(100) NOT NULL,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    UNIQUE (tenant_id, code)
);
CREATE INDEX idx_cycle_tenant ON cycle(tenant_id);

-- Niveau scolaire (6ème, 5ème, 2nde, etc.)
CREATE TABLE niveau (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    cycle_id        UUID NOT NULL REFERENCES cycle(id),
    code            VARCHAR(20) NOT NULL,
    libelle         VARCHAR(100) NOT NULL,
    ordre           INTEGER NOT NULL,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    UNIQUE (tenant_id, cycle_id, code)
);
CREATE INDEX idx_niveau_tenant ON niveau(tenant_id);
CREATE INDEX idx_niveau_cycle ON niveau(cycle_id);

-- Bâtiment
CREATE TABLE batiment (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nom             VARCHAR(100) NOT NULL,
    description     TEXT,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);
CREATE INDEX idx_batiment_tenant ON batiment(tenant_id);

-- Salle
CREATE TABLE salle (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    batiment_id     UUID NOT NULL REFERENCES batiment(id),
    nom             VARCHAR(50) NOT NULL,
    capacite        INTEGER,
    type_salle      VARCHAR(50),
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);
CREATE INDEX idx_salle_tenant ON salle(tenant_id);
CREATE INDEX idx_salle_batiment ON salle(batiment_id);

-- Année académique
CREATE TABLE annee_academique (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    libelle         VARCHAR(20) NOT NULL,
    date_debut      DATE NOT NULL,
    date_fin        DATE NOT NULL,
    est_courante    BOOLEAN NOT NULL DEFAULT FALSE,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    UNIQUE (tenant_id, libelle)
);
CREATE INDEX idx_annee_academique_tenant ON annee_academique(tenant_id);

-- tenant_id sur classes
ALTER TABLE classes ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenant(id);
UPDATE classes SET tenant_id = (SELECT id FROM tenant WHERE slug = 'default' LIMIT 1) WHERE tenant_id IS NULL;
ALTER TABLE classes ALTER COLUMN tenant_id SET NOT NULL;
CREATE INDEX IF NOT EXISTS idx_classes_tenant ON classes(tenant_id);

-- colonnes optionnelles pour migration progressive : niveau_id, annee_academique_id, salle_id
ALTER TABLE classes ADD COLUMN IF NOT EXISTS niveau_id UUID REFERENCES niveau(id);
ALTER TABLE classes ADD COLUMN IF NOT EXISTS annee_academique_id UUID REFERENCES annee_academique(id);
ALTER TABLE classes ADD COLUMN IF NOT EXISTS salle_id UUID REFERENCES salle(id);

-- tenant_id sur matieres
ALTER TABLE matieres ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenant(id);
UPDATE matieres SET tenant_id = (SELECT id FROM tenant WHERE slug = 'default' LIMIT 1) WHERE tenant_id IS NULL;
ALTER TABLE matieres ALTER COLUMN tenant_id SET NOT NULL;
CREATE INDEX IF NOT EXISTS idx_matieres_tenant ON matieres(tenant_id);

-- tenant_id sur matiere_classe
ALTER TABLE matiere_classe ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenant(id);
UPDATE matiere_classe SET tenant_id = (SELECT id FROM tenant WHERE slug = 'default' LIMIT 1) WHERE tenant_id IS NULL;
ALTER TABLE matiere_classe ALTER COLUMN tenant_id SET NOT NULL;
CREATE INDEX IF NOT EXISTS idx_matiere_classe_tenant ON matiere_classe(tenant_id);

-- tenant_id sur emplois_du_temps
ALTER TABLE emplois_du_temps ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenant(id);
UPDATE emplois_du_temps SET tenant_id = (SELECT id FROM tenant WHERE slug = 'default' LIMIT 1) WHERE tenant_id IS NULL;
ALTER TABLE emplois_du_temps ALTER COLUMN tenant_id SET NOT NULL;
CREATE INDEX IF NOT EXISTS idx_emplois_du_temps_tenant ON emplois_du_temps(tenant_id);

-- Cours (Matière + Professeur + Classe + Année académique)
-- Référence matieres(id), classes(id), users(id) pour professeur
CREATE TABLE cours (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL REFERENCES tenant(id),
    matiere_id          UUID NOT NULL REFERENCES matieres(id),
    professeur_id       UUID NOT NULL REFERENCES users(id),
    classe_id           UUID NOT NULL REFERENCES classes(id),
    annee_academique_id UUID NOT NULL REFERENCES annee_academique(id),
    volume_horaire_hebdo DECIMAL(4,1),
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP,
    UNIQUE (matiere_id, professeur_id, classe_id, annee_academique_id)
);
CREATE INDEX idx_cours_tenant ON cours(tenant_id);
CREATE INDEX idx_cours_classe_annee ON cours(classe_id, annee_academique_id);
CREATE INDEX idx_cours_matiere ON cours(matiere_id);
