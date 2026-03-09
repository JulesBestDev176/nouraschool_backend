-- notification_log : traçabilité des envois (task.md section 15)
-- Toute notification est enregistrée ici avant envoi (WHATSAPP, SMS, EMAIL, IN_APP)
CREATE TABLE notification_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID,
    canal           VARCHAR(20) NOT NULL,
    destinataire    VARCHAR(254),
    sujet           VARCHAR(255),
    contenu         TEXT,
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    erreur          TEXT,
    envoye_le       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notification_log_tenant ON notification_log(tenant_id, created_at DESC);
CREATE INDEX idx_notification_log_statut ON notification_log(statut) WHERE statut = 'EN_ATTENTE';
