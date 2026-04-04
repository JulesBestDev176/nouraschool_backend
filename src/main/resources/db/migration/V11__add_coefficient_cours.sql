-- Coefficient par cours (pour pondération des notes)
ALTER TABLE cours ADD COLUMN IF NOT EXISTS coefficient DECIMAL(4,2) DEFAULT 1.0;
