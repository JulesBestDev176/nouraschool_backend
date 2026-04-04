-- =============================================================================
-- V16: Vie scolaire - convocation, lien_bulletin_parent, colonnes emplois_du_temps, absences_eleves, bulletins
-- =============================================================================

-- 1. convocation table (task.md)
CREATE TABLE convocation (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    parent_id       UUID NOT NULL REFERENCES parents(id),
    eleve_id        UUID NOT NULL REFERENCES eleves(id),
    motif           TEXT NOT NULL,
    date_convocation TIMESTAMPTZ NOT NULL,
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    compte_rendu    TEXT,
    cree_par        UUID NOT NULL REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_convocation_tenant ON convocation(tenant_id);
CREATE INDEX idx_convocation_parent ON convocation(parent_id);
CREATE INDEX idx_convocation_eleve ON convocation(eleve_id);

-- 2. lien_bulletin_parent table (secure bulletin link for parent OTP flow)
CREATE TABLE lien_bulletin_parent (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    bulletin_id     UUID NOT NULL REFERENCES bulletins(id),
    parent_id       UUID NOT NULL REFERENCES parents(id),
    token           VARCHAR(255) UNIQUE NOT NULL,
    otp_hash        VARCHAR(255),
    otp_expires_at  TIMESTAMPTZ,
    otp_verified    BOOLEAN DEFAULT FALSE,
    expires_at      TIMESTAMPTZ NOT NULL,
    ouvert_le       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_lien_bulletin_parent_tenant ON lien_bulletin_parent(tenant_id);
CREATE INDEX idx_lien_bulletin_parent_bulletin ON lien_bulletin_parent(bulletin_id);
CREATE INDEX idx_lien_bulletin_parent_parent ON lien_bulletin_parent(parent_id);
CREATE INDEX idx_lien_bulletin_parent_token ON lien_bulletin_parent(token);

-- 3. Add publie to emplois_du_temps
ALTER TABLE emplois_du_temps ADD COLUMN IF NOT EXISTS publie BOOLEAN DEFAULT FALSE;

-- 4. Add statut and approuve_par to absences_eleves
ALTER TABLE absences_eleves ADD COLUMN IF NOT EXISTS statut VARCHAR(20) DEFAULT 'EN_ATTENTE';
ALTER TABLE absences_eleves ADD COLUMN IF NOT EXISTS approuve_par UUID REFERENCES users(id);

-- 5. Add statut, soumis_par, valide_par to bulletins
ALTER TABLE bulletins ADD COLUMN IF NOT EXISTS statut VARCHAR(20) DEFAULT 'BROUILLON';
ALTER TABLE bulletins ADD COLUMN IF NOT EXISTS soumis_par UUID REFERENCES users(id);
ALTER TABLE bulletins ADD COLUMN IF NOT EXISTS valide_par UUID REFERENCES users(id);
