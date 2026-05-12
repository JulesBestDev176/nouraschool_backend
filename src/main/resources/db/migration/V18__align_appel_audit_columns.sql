-- Align V17 tables with AbstractEntity mapping used by Hibernate validation.
-- AbstractEntity.createdAt / updatedAt are nullable in the Java model.

ALTER TABLE cahier_texte ALTER COLUMN created_at DROP NOT NULL;

ALTER TABLE appel ALTER COLUMN created_at DROP NOT NULL;
ALTER TABLE appel ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

ALTER TABLE appel_ligne ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;
ALTER TABLE appel_ligne ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;
