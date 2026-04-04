-- =============================================================================
-- V17: Espace professeur - appel, appel_ligne, cahier_texte
-- =============================================================================

-- Cahier de texte (professeur)
CREATE TABLE cahier_texte (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    cours_id        UUID NOT NULL REFERENCES cours(id),
    date_cours      DATE NOT NULL,
    contenu_traite  TEXT,
    observations    TEXT,
    etape_programme VARCHAR(255),
    programme_valide BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);
CREATE INDEX idx_cahier_texte_tenant ON cahier_texte(tenant_id);
CREATE INDEX idx_cahier_texte_cours ON cahier_texte(cours_id);
CREATE INDEX idx_cahier_texte_date ON cahier_texte(date_cours);

-- Appel (émargement)
CREATE TABLE appel (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    cours_id        UUID NOT NULL REFERENCES cours(id),
    date_cours      DATE NOT NULL,
    heure_debut     TIME,
    statut          VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    soumis_par      UUID NOT NULL REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_appel_tenant ON appel(tenant_id);
CREATE INDEX idx_appel_cours ON appel(cours_id);
CREATE INDEX idx_appel_date ON appel(date_cours);

-- Ligne d'appel (présent/absent/retard par élève)
CREATE TABLE appel_ligne (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    appel_id        UUID NOT NULL REFERENCES appel(id) ON DELETE CASCADE,
    eleve_id        UUID NOT NULL REFERENCES eleves(id),
    statut          VARCHAR(20) NOT NULL,
    UNIQUE (appel_id, eleve_id)
);
CREATE INDEX idx_appel_ligne_appel ON appel_ligne(appel_id);
CREATE INDEX idx_appel_ligne_eleve ON appel_ligne(eleve_id);
