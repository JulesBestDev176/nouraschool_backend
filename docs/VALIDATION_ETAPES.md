# Validation des étapes — Référence task.md

Document de référence : **`task.md`** (racine du projet).  
Chaque étape est cochée lorsqu’elle est implémentée et validée.

---

## Phase 0 — Conventions (Section 3 task.md)

### 3.1 Nommage
- [ ] Tables SQL : `snake_case`
- [ ] Colonnes SQL : `snake_case`
- [ ] Endpoints REST : `/api/v1/...`, kebab-case, pluriel pour les ressources
- [ ] Enums Java : `UPPER_SNAKE_CASE`

### 3.2 IDs
- [ ] Tous les IDs en **UUID v4**, générés côté serveur
- [ ] Stockage PostgreSQL en type UUID, sérialisation JSON en String

### 3.3 Dates
- [ ] Stockage en **UTC** (TIMESTAMPTZ)
- [ ] Format JSON : ISO 8601 (`2025-06-01T10:30:00Z`)
- [ ] Types Java : `Instant` (timestamps), `LocalDate` (dates seules)
- [ ] Colonnes `created_at`, `updated_at` sur toutes les entités
- [ ] Soft delete : `deleted_at` (jamais de DELETE physique en prod)

### 3.4 Endpoints REST
- [ ] Préfixe **`/api/v1/`**
- [ ] Ressources en kebab-case pluriel
- [ ] Pagination : `?page=0&size=20`, tri `?sort=nom,asc`

### 3.5 Logging
- [ ] Format : `[NomClasse][nomMethode] message {}`
- [ ] Logger par classe, entrée/sortie/erreur des méthodes de service
- [ ] Aucune donnée sensible (mot de passe, token) dans les logs

### 3.6 Format de réponse standard
- [ ] Liste : `{ "data": [...], "page", "size", "total", "totalPages" }`
- [ ] Objet : `{ "data": { ... } }`
- [x] Erreur : `{ "code", "message", "details", "correlationId", "timestamp" }` (ErrorDto + DefaultExceptionHandler)

### 3.7 Codes d'erreur métier
- [x] `IDENTIFIANTS_INVALIDES` (401), `COMPTE_VERROUILLE` (423), `TOKEN_EXPIRE` (401)
- [x] `ACCES_REFUSE` (403), `RESSOURCE_INTROUVABLE` (404), `EMAIL_DEJA_UTILISE` (409)
- [x] `VALIDATION_ECHOUEE` (400), `REGLE_METIER_VIOLEE` (422), `TENANT_INACTIF` (403)

---

## Phase 1 — Modèle de données (Section 4 task.md) — minimal Auth

### 4.1 Tables plateforme
- [x] Table **`tenant`** (id, slug, nom, email_contact, telephone, adresse, logo_url, plan, actif, date_expiration, created_at, updated_at, deleted_at)
- [x] Table **`plateforme_utilisateur`** (id, nom, prenom, email, mot_de_passe, role_plateforme, actif, created_at, updated_at, deleted_at)

### 4.2 Tables tenant (minimal pour Auth)
- [x] Colonnes **tenant_id** et **must_change_password** sur `users` (alignement progressif ; table `utilisateur` = `users` existant)
- [x] Table **`refresh_token`** existante (nom actuel `refresh_tokens`)
- [x] Table **`audit_log`** (id, tenant_id, utilisateur_id, role, action, resource_type, resource_id, details, ip_address, user_agent, created_at)

### 4.3 Index
- [x] `idx_users_tenant_id`, `idx_refresh_token_hash`, `idx_audit_log_tenant`, `idx_audit_log_utilisateur`

---

## Phase 2 — Module 1 Authentification (Section 6 task.md)

### UC-AUTH-01 : Login
- [x] Connexion **email + mot de passe** (username ou email accepté pour compatibilité)
- [x] Vérification bcrypt (cost configurable)
- [x] Vérification `actif = true`
- [ ] Si tenant : vérification `tenant.actif = true` (à brancher quand TenantResolver en place)
- [x] Access token JWT (RS256, 15 min), refresh token opaque (UUID, 7 j, stocké hashé)
- [x] Si `must_change_password = true` → flag `passwordChangeRequired: true` dans la réponse
- [ ] Audit `LOGIN_SUCCESS` (table audit_log en place, à appeler dans le service)
- [ ] Échec : compteur tentatives (5 en 10 min → verrouillage 30 min), audit `LOGIN_FAILURE`
- [x] Message d'erreur générique : "Identifiants invalides" (code IDENTIFIANTS_INVALIDES)

### UC-AUTH-02 : Refresh token
- [ ] Vérification token opaque (hash en base, non révoqué, non expiré)
- [ ] **Rotation stricte** : révoquer l'ancien, émettre nouveau access + nouveau refresh
- [ ] Si token déjà révoqué présenté → invalider **tous** les refresh tokens du user (détection de vol)

### UC-AUTH-03 : Logout
- [ ] Révocation du refresh token en base
- [ ] Audit `LOGOUT`

### UC-AUTH-04 : Mot de passe oublié
- [ ] Body : email. Réponse anti-enumeration : "Si cet email existe, un lien a été envoyé"
- [ ] Token reset (UUID, 1 h, Redis ou table)
- [ ] Envoi email avec lien (implémentation ou mock)

### UC-AUTH-05 : Réinitialisation mot de passe
- [ ] Validation token reset (usage unique)
- [ ] Nouveau MDP : min 12 car., maj + chiffre + spécial, bcrypt, `must_change_password = false`
- [ ] Invalidation de tous les refresh tokens actifs, audit `PASSWORD_RESET_SUCCESS`

### UC-AUTH-06 : Changement de mot de passe forcé (1ère connexion)
- [ ] Endpoint `POST /api/v1/auth/change-password` (quand `must_change_password = true`)

### UC-AUTH-07 : Profil connecté
- [x] `GET /api/v1/auth/me` — infos du token courant (AuthMeDto : id, nom, prenom, email, role, tenantId, cycles, actif)

### Endpoints Auth
- [x] `POST /api/v1/auth/login`
- [x] `POST /api/v1/auth/refresh`
- [x] `POST /api/v1/auth/logout`
- [ ] `POST /api/v1/auth/forgot-password`
- [ ] `POST /api/v1/auth/reset-password`
- [ ] `POST /api/v1/auth/change-password`
- [x] `GET /api/v1/auth/me`

### Claims JWT
- [ ] `sub`, `tenantId`, `role`, `cycles` (si SURVEILLANT), `email`, `nom`, `prenom`, `iat`, `exp`, `iss`, `jti`

### Rate limiting Auth
- [ ] Login : 5 tentatives / IP / 10 min
- [ ] Refresh : 30 req / token / 1 h
- [ ] Forgot-password : 3 req / email / 15 min

---

## Phase 3 — Module 2 Gestion plateforme (Section 7) — à venir
- [ ] UC-PLAT-01 à 07
- [ ] Endpoints `/api/platform/tenants`, `/api/platform/utilisateurs`, etc.

---

## Phase 4 — Module 3 Gestion établissement (Section 8) — à venir
- [ ] UC-ADMIN-01 à 08
- [ ] Endpoints cycles, niveaux, matières, classes, utilisateurs, surveillant_cycle, etc.

---

*Référence : task.md · Cocher au fur et à mesure des validations.*
