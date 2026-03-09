-- Personnel : fiche RH (type contrat, salaire, solde congés)
CREATE TABLE personnel (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    utilisateur_id  UUID NOT NULL REFERENCES users(id),
    numero_matricule VARCHAR(50),
    type_contrat    VARCHAR(50),
    date_embauche   DATE,
    salaire         DECIMAL(10,2),
    solde_conge     INTEGER DEFAULT 0,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);
CREATE INDEX idx_personnel_tenant ON personnel(tenant_id);
CREATE INDEX idx_personnel_utilisateur ON personnel(utilisateur_id);

-- Pointage : entrées/sorties
CREATE TABLE pointage (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    utilisateur_id  UUID NOT NULL REFERENCES users(id),
    type_pointage   VARCHAR(10) NOT NULL,
    date_heure      TIMESTAMPTZ NOT NULL,
    methode         VARCHAR(20),
    created_by      UUID REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_pointage_tenant ON pointage(tenant_id);
CREATE INDEX idx_pointage_utilisateur ON pointage(utilisateur_id);
CREATE INDEX idx_pointage_date ON pointage(date_heure);

-- Absence personnel
CREATE TABLE absence_personnel (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    utilisateur_id  UUID NOT NULL REFERENCES users(id),
    date_debut      DATE NOT NULL,
    date_fin        DATE NOT NULL,
    motif           VARCHAR(255),
    type_absence    VARCHAR(50),
    justificatif_url VARCHAR(500),
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    valide_par      UUID REFERENCES users(id),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);
CREATE INDEX idx_absence_personnel_tenant ON absence_personnel(tenant_id);
CREATE INDEX idx_absence_personnel_utilisateur ON absence_personnel(utilisateur_id);
