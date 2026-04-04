-- Hibernate 6 expects sequences named {table_name}_seq (e.g. absences_eleves_seq).
-- PostgreSQL BIGSERIAL creates {table_name}_id_seq. We create the expected sequences
-- and switch the column defaults so that Hibernate validation passes and inserts work.

-- users (BIGSERIAL in V1)
CREATE SEQUENCE IF NOT EXISTS users_seq;
SELECT setval('users_seq', COALESCE((SELECT MAX(id) FROM users), 1));
ALTER TABLE users ALTER COLUMN id SET DEFAULT nextval('users_seq');

-- classes
CREATE SEQUENCE IF NOT EXISTS classes_seq;
SELECT setval('classes_seq', COALESCE((SELECT MAX(id) FROM classes), 1));
ALTER TABLE classes ALTER COLUMN id SET DEFAULT nextval('classes_seq');

-- refresh_tokens
CREATE SEQUENCE IF NOT EXISTS refresh_tokens_seq;
SELECT setval('refresh_tokens_seq', COALESCE((SELECT MAX(id) FROM refresh_tokens), 1));
ALTER TABLE refresh_tokens ALTER COLUMN id SET DEFAULT nextval('refresh_tokens_seq');

-- matieres
CREATE SEQUENCE IF NOT EXISTS matieres_seq;
SELECT setval('matieres_seq', COALESCE((SELECT MAX(id) FROM matieres), 1));
ALTER TABLE matieres ALTER COLUMN id SET DEFAULT nextval('matieres_seq');

-- matiere_classe
CREATE SEQUENCE IF NOT EXISTS matiere_classe_seq;
SELECT setval('matiere_classe_seq', COALESCE((SELECT MAX(id) FROM matiere_classe), 1));
ALTER TABLE matiere_classe ALTER COLUMN id SET DEFAULT nextval('matiere_classe_seq');

-- emplois_du_temps
CREATE SEQUENCE IF NOT EXISTS emplois_du_temps_seq;
SELECT setval('emplois_du_temps_seq', COALESCE((SELECT MAX(id) FROM emplois_du_temps), 1));
ALTER TABLE emplois_du_temps ALTER COLUMN id SET DEFAULT nextval('emplois_du_temps_seq');

-- notes
CREATE SEQUENCE IF NOT EXISTS notes_seq;
SELECT setval('notes_seq', COALESCE((SELECT MAX(id) FROM notes), 1));
ALTER TABLE notes ALTER COLUMN id SET DEFAULT nextval('notes_seq');

-- absences_eleves
CREATE SEQUENCE IF NOT EXISTS absences_eleves_seq;
SELECT setval('absences_eleves_seq', COALESCE((SELECT MAX(id) FROM absences_eleves), 1));
ALTER TABLE absences_eleves ALTER COLUMN id SET DEFAULT nextval('absences_eleves_seq');

-- absences_enseignants
CREATE SEQUENCE IF NOT EXISTS absences_enseignants_seq;
SELECT setval('absences_enseignants_seq', COALESCE((SELECT MAX(id) FROM absences_enseignants), 1));
ALTER TABLE absences_enseignants ALTER COLUMN id SET DEFAULT nextval('absences_enseignants_seq');

-- bulletins
CREATE SEQUENCE IF NOT EXISTS bulletins_seq;
SELECT setval('bulletins_seq', COALESCE((SELECT MAX(id) FROM bulletins), 1));
ALTER TABLE bulletins ALTER COLUMN id SET DEFAULT nextval('bulletins_seq');

-- reclamations
CREATE SEQUENCE IF NOT EXISTS reclamations_seq;
SELECT setval('reclamations_seq', COALESCE((SELECT MAX(id) FROM reclamations), 1));
ALTER TABLE reclamations ALTER COLUMN id SET DEFAULT nextval('reclamations_seq');

-- paiements
CREATE SEQUENCE IF NOT EXISTS paiements_seq;
SELECT setval('paiements_seq', COALESCE((SELECT MAX(id) FROM paiements), 1));
ALTER TABLE paiements ALTER COLUMN id SET DEFAULT nextval('paiements_seq');

-- notifications
CREATE SEQUENCE IF NOT EXISTS notifications_seq;
SELECT setval('notifications_seq', COALESCE((SELECT MAX(id) FROM notifications), 1));
ALTER TABLE notifications ALTER COLUMN id SET DEFAULT nextval('notifications_seq');

-- calendrier_scolaire
CREATE SEQUENCE IF NOT EXISTS calendrier_scolaire_seq;
SELECT setval('calendrier_scolaire_seq', COALESCE((SELECT MAX(id) FROM calendrier_scolaire), 1));
ALTER TABLE calendrier_scolaire ALTER COLUMN id SET DEFAULT nextval('calendrier_scolaire_seq');
