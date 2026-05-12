-- Ensure the global platform administrator exists even on already deployed databases.
-- Password: SuperAdmin123! (must be changed immediately after first login).
INSERT INTO plateforme_utilisateur (
    id,
    nom,
    prenom,
    email,
    mot_de_passe,
    role_plateforme,
    actif,
    created_at
)
SELECT
    gen_random_uuid(),
    'Platform',
    'Admin',
    'superadmin@noura-school.com',
    '$2a$12$ePa9sWoHa.1SvnIWh2iT4uoJkkK4Y1em/TBR6KYFg0ZM6ge5MvjDm',
    'SUPER_ADMIN',
    TRUE,
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM plateforme_utilisateur
    WHERE email = 'superadmin@noura-school.com'
);
