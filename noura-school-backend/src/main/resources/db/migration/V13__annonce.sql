-- V13 : Table annonce (tableau d'affichage visible par profs/élèves)
CREATE TABLE annonce (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    titre       VARCHAR(255) NOT NULL,
    contenu     TEXT,
    date_debut  DATE,
    date_fin    DATE,
    actif       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);
CREATE INDEX idx_annonce_tenant ON annonce(tenant_id);
CREATE INDEX idx_annonce_dates ON annonce(date_debut, date_fin);
