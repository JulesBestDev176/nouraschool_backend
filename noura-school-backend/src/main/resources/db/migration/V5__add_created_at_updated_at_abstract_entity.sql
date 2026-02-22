-- Add created_at / updated_at to tables whose entities extend AbstractEntity (V4 omitted some columns).

-- absences_eleves: had created_at only
ALTER TABLE absences_eleves ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- absences_enseignants: had created_at only
ALTER TABLE absences_enseignants ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- bulletins: had created_at only
ALTER TABLE bulletins ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- refresh_tokens: had created_at only
ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- paiements: had created_at only
ALTER TABLE paiements ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- notifications: had created_at only
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- classes: had neither
ALTER TABLE classes ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE classes ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- matieres: had neither
ALTER TABLE matieres ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE matieres ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- matiere_classe: had neither
ALTER TABLE matiere_classe ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE matiere_classe ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- emplois_du_temps: had neither
ALTER TABLE emplois_du_temps ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE emplois_du_temps ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- calendrier_scolaire: had neither
ALTER TABLE calendrier_scolaire ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE calendrier_scolaire ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
