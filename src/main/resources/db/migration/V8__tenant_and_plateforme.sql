-- Tables plateforme (task.md section 4.1)
CREATE TABLE tenant (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug            VARCHAR(100) UNIQUE NOT NULL,
    nom             VARCHAR(255) NOT NULL,
    email_contact   VARCHAR(254),
    telephone       VARCHAR(20),
    adresse         TEXT,
    logo_url        VARCHAR(500),
    plan            VARCHAR(50) NOT NULL DEFAULT 'TRIAL',
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    date_expiration DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

CREATE TABLE plateforme_utilisateur (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100) NOT NULL,
    email           VARCHAR(254) UNIQUE NOT NULL,
    mot_de_passe    VARCHAR(255) NOT NULL,
    role_plateforme VARCHAR(50) NOT NULL,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

-- Tenant par défaut pour données existantes
INSERT INTO tenant (id, slug, nom, plan, actif) VALUES
(gen_random_uuid(), 'default', 'Établissement par défaut', 'TRIAL', TRUE);

-- Colonnes utilisateur (task.md) : tenant_id, must_change_password
ALTER TABLE users ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenant(id);
ALTER TABLE users ADD COLUMN IF NOT EXISTS must_change_password BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE users SET tenant_id = (SELECT id FROM tenant WHERE slug = 'default' LIMIT 1) WHERE tenant_id IS NULL;
ALTER TABLE users ALTER COLUMN tenant_id SET NOT NULL;

-- Contrainte unique email par tenant (task.md)
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_email_key;
ALTER TABLE users ADD CONSTRAINT users_tenant_email_key UNIQUE (tenant_id, email);

CREATE INDEX IF NOT EXISTS idx_users_tenant_id ON users(tenant_id);

-- Audit log (task.md section 4.2)
CREATE TABLE audit_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID,
    utilisateur_id  UUID,
    role            VARCHAR(50),
    action          VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(100),
    resource_id     UUID,
    details         JSONB,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_audit_log_tenant ON audit_log(tenant_id, created_at DESC);
CREATE INDEX idx_audit_log_utilisateur ON audit_log(utilisateur_id, created_at DESC);
