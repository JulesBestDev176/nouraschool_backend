-- Force reset du super admin plateforme pour garantir un hash bcrypt valide.
-- DataSeedService recrée le compte au démarrage avec passwordEncoder.encode().
DELETE FROM plateforme_utilisateur WHERE email = 'superadmin@noura-school.com';
