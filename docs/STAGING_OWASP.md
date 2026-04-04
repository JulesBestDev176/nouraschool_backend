# Staging & revue sécurité OWASP — Noura School Backend

## Checklist OWASP (task.md FOLDER 13)

### A01 — Broken Access Control
- [x] RBAC par rôle (ADMIN, SURVEILLANT, ENSEIGNANT, ELEVE, etc.)
- [x] Isolation multi-tenant (tenant_id sur les requêtes)
- [ ] Vérifier que chaque endpoint valide le tenant et le rôle

### A02 — Cryptographic Failures
- [x] Mots de passe hashés (bcrypt)
- [x] JWT RS256 (clé asymétrique)
- [ ] HTTPS obligatoire en production

### A03 — Injection
- [x] Hibernate/JPA (requêtes paramétrées)
- [x] Pas de SQL brut

### A04 — Insecure Design
- [ ] Validation des entrées (Jakarta Validation)
- [ ] Rate limiting (Redis) sur login, forgot-password

### A05 — Security Misconfiguration
- [x] Swagger désactivé en prod
- [x] Headers de sécurité (HSTS, X-Frame-Options, etc.)
- [ ] Secrets via variables d'environnement

### A06 — Vulnerable Components
- [ ] `mvn dependency:analyze` — vérifier dépendances obsolètes
- [ ] Mise à jour régulière des dépendances

### A07 — Auth Failures
- [x] Verrouillage compte après 5 tentatives
- [x] Refresh token rotation
- [ ] Expiration OTP 5 min, max 3 tentatives

## Staging

Avant mise en production :
1. Déployer sur environnement staging
2. Exécuter scan OWASP ZAP ou équivalent
3. Corriger les vulnérabilités critiques/haute
4. Valider avec checklist ci-dessus
