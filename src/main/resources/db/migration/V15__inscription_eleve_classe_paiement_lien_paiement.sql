-- =============================================================================
-- V15: Inscriptions, paiements, lien_paiement_parent
-- =============================================================================

-- 1. Inscription table (eleve-class enrollment per academic year)
-- inscription IS the enrollment; no separate eleve_classe table
CREATE TABLE inscription (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL REFERENCES tenant(id),
    numero_inscription  VARCHAR(50) UNIQUE NOT NULL,
    eleve_id            UUID NOT NULL REFERENCES eleves(id),
    classe_id           UUID NOT NULL REFERENCES classes(id),
    annee_academique_id UUID NOT NULL REFERENCES annee_academique(id),
    statut              VARCHAR(20) NOT NULL DEFAULT 'ACTIF',  -- ACTIF | TRANSFERE | EXCLU
    cree_par            UUID NOT NULL REFERENCES users(id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ
);
CREATE INDEX idx_inscription_tenant ON inscription(tenant_id);
CREATE INDEX idx_inscription_eleve ON inscription(eleve_id);
CREATE INDEX idx_inscription_classe ON inscription(classe_id);
CREATE INDEX idx_inscription_annee_academique ON inscription(annee_academique_id);

-- 2. paiements: add tenant_id (nullable first, backfill, then NOT NULL) and inscription_id
ALTER TABLE paiements ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenant(id);
UPDATE paiements SET tenant_id = (SELECT id FROM tenant WHERE slug = 'default' LIMIT 1) WHERE tenant_id IS NULL;
ALTER TABLE paiements ALTER COLUMN tenant_id SET NOT NULL;
CREATE INDEX IF NOT EXISTS idx_paiements_tenant ON paiements(tenant_id);

ALTER TABLE paiements ADD COLUMN IF NOT EXISTS inscription_id UUID REFERENCES inscription(id);

-- 3. lien_paiement_parent (secure payment link for parent OTP flow)
CREATE TABLE lien_paiement_parent (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    parent_id       UUID NOT NULL REFERENCES parents(id),
    token           VARCHAR(255) UNIQUE NOT NULL,
    montant_total   DECIMAL(10,2) NOT NULL,
    mois            VARCHAR(7) NOT NULL,   -- e.g. 2025-06
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    expires_at      TIMESTAMPTZ NOT NULL,
    otp_hash        VARCHAR(255),
    otp_expires_at  TIMESTAMPTZ,
    otp_verified    BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_lien_paiement_parent_tenant ON lien_paiement_parent(tenant_id);
CREATE INDEX idx_lien_paiement_parent_parent ON lien_paiement_parent(parent_id);
CREATE INDEX idx_lien_paiement_parent_token ON lien_paiement_parent(token);
CREATE INDEX idx_lien_paiement_parent_expires_at ON lien_paiement_parent(expires_at);
