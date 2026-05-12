-- Garantit un hash bcrypt $2a$12$ valide pour SuperAdmin123!
-- Compatible avec org.mindrot.jbcrypt.BCrypt utilisé par l'application.
DELETE FROM plateforme_utilisateur WHERE email = 'superadmin@noura-school.com';

INSERT INTO plateforme_utilisateur (nom, prenom, email, mot_de_passe, role_plateforme, actif, created_at)
VALUES (
    'Platform',
    'Admin',
    'superadmin@noura-school.com',
    '$2a$12$YlIveARWj42du9K4LgIloei8zXFtpkMxv4j6Z4eK9MqZWR5DmIit2',
    'SUPER_ADMIN',
    TRUE,
    NOW()
);
