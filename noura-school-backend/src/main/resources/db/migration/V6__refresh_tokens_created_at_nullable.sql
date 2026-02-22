-- Align DB with JPA model: AbstractEntity.createdAt has no nullable=false,
-- so refresh_tokens.created_at must be nullable for schema validation.
ALTER TABLE refresh_tokens ALTER COLUMN created_at DROP NOT NULL;
