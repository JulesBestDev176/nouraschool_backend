# Suivi du projet Noura School Backend

Document de suivi : contexte, étapes validées et liens vers la spec et les tâches.

---

## Documents de référence

| Document | Rôle |
|----------|------|
| **`task.md`** | Document de référence global (vision, use cases, schémas SQL) |
| **`docs/TACHES_CLICKUP.md`** | Liste des tâches par folder (validation par étape) |
| **`docs/SPEC_AUTH_ET_ROLES.md`** | Spec auth, rôles, multi-tenant, sécurité |
| **`docs/SUIVI_PROJET.md`** | Ce fichier — résumé et étapes validées |
| **`docs/DOCUMENTATION_GLOBALE.md`** | Documentation globale (frontend, non-dev, API, reste à faire) |
| **`docs/DEPLOIEMENT.md`** | Guide de déploiement |
| **`docs/MONITORING.md`** | Health checks, alertes (5xx, latence) |
| **`docs/STAGING_OWASP.md`** | Checklist OWASP, revue sécurité |

---

## Contexte métier (résumé)

- **Noura School** : plateforme SaaS multi-tenant pour établissements scolaires (collège & lycée).
- **Admin** crée : caissiers, surveillants, enseignants, élèves, parents.
- **Caissier** crée : élèves, parents (inscriptions).
- **Enseignant** : appels, cahier de texte, notes, emploi du temps.
- **Élève** : profil, notes, bulletins validés, absences, réclamations, notifications.
- **Parent** : pas de connexion ; accès prévu par OTP (bulletin, paiement).

---

## Rôles et droits (état actuel)

| Rôle        | Créé par            | Peut se connecter |
|-------------|---------------------|-------------------|
| ADMIN       | Seed / autre admin  | Oui               |
| CAISSIER    | Admin               | Oui               |
| SURVEILLANT | Admin               | Oui               |
| ENSEIGNANT  | Admin               | Oui               |
| ELEVE       | Admin ou Caissier   | Oui               |
| PARENT      | Admin ou Caissier   | Non (OTP plus tard)|

---

## Étapes validées

### Folders 1–8 (Setup, Fondations, Auth, Plateforme, Établissement, RH, Inscriptions, Vie scolaire)

- [x] Quarkus 3.x, Java 21, PostgreSQL, Redis, Docker, Flyway
- [x] Multi-tenant (TenantContext, TenantResolver, X-Tenant-Id)
- [x] Auth JWT, refresh token, logout, forgot/reset password, /me
- [x] Gestion des exceptions, Correlation ID, Audit log, S3
- [x] Cycles, niveaux, bâtiments, salles, années académiques, cours
- [x] Personnel, pointage, absences personnel
- [x] Inscriptions, paiements, liens paiement OTP
- [x] Notes, bulletins, absences élèves, convocations, emplois du temps

### Folder 9 — Espace Professeur

- [x] Migration `appel`, `appel_ligne`, `cahier_texte`
- [x] AppelService : créer appel, soumettre (création absences auto pour lignes ABSENT)
- [x] CahierTexteService, NoteProfesseurService (EnseignantUseCase)
- [x] Endpoints : appels, cahier-texte, emploi-du-temps

### Folder 10 — Espace Élève

- [x] EleveUseCase : mon profil, mes notes, mes bulletins (validés uniquement), mes absences, réclamations, notifications
- [x] PATCH /notifications/{id}/lire
- [x] Bulletin non validé filtré (statut VALIDE)

### Folder 11 — Notifications

- [x] EmailService (SMTP, templates compte, reset mdp, bulletin)
- [x] OtpService (Redis, 6 chiffres, TTL 5 min, max 3 tentatives)
- [x] WhatsAppService branché sur un gateway Node whatsapp-web.js pour OTP

### Folder 12 — Tests & Qualité

- [x] @QuarkusTest, TestContainers, fixtures, isolation cross-tenant
- [x] Tests Professeur (appel → absences auto, périmètre)
- [x] Tests Élève (données isolées, bulletin non validé)
- [x] Gatling (simulation de charge)
- [x] Annotations OpenAPI sur endpoints
- [x] Guide de déploiement

### Folder 13 — Déploiement & Production

- [x] application-prod.yml (secrets via env, logs JSON, Swagger désactivé)
- [x] Health checks `/q/health/live`, `/q/health/ready`
- [x] Docs MONITORING (alertes 5xx, latence), STAGING_OWASP

---

## Alignement spec auth (SPEC_AUTH_ET_ROLES.md)

- [x] Cycles dans `/me` pour rôle SURVEILLANT
- [x] Protection du dernier ADMIN (désactivation + suppression)
- [x] Politique mot de passe 12 caractères min (reset, change, reinitialiserMdp)
- [ ] IP/User-Agent sur refresh_tokens (audit) — prévu

Voir section 12 « État d'implémentation » dans SPEC_AUTH_ET_ROLES.md.

---

## À faire (voir TACHES_CLICKUP.md)

- Période verrouillée pour saisie de notes (validation métier)
- Déploiement et supervision du gateway WhatsApp OTP
- Tests Gatling 100 tenants simultanés (simulation étendue)
- Staging + scan OWASP ZAP

---

## Mise à jour

- Cocher les cases dans **TACHES_CLICKUP.md** quand une tâche est validée.
- Mettre à jour ce fichier (SUIVI_PROJET.md) lors des jalons importants.
