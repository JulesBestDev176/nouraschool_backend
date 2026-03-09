# NOURA SCHOOL — Document de référence Backend

> **Java 21 · Quarkus 3.x · PostgreSQL · JWT · REST API**
> Plateforme SaaS multi-tenant de gestion scolaire (collège & lycée).
> Ce document est la source de vérité du projet backend — du premier commit à la mise en production.

---

## Table des matières

1. [Vision produit & roadmap](#1-vision-produit--roadmap)
2. [Architecture globale](#2-architecture-globale)
3. [Conventions du projet](#3-conventions-du-projet)
4. [Modèle de données global](#4-modèle-de-données-global)
5. [Acteurs & rôles — Analyse complète](#5-acteurs--rôles--analyse-complète)
6. [Module 1 — Authentification](#6-module-1--authentification)
7. [Module 2 — Gestion plateforme (Super Admin)](#7-module-2--gestion-plateforme-super-admin)
8. [Module 3 — Gestion établissement (Admin)](#8-module-3--gestion-établissement-admin)
9. [Module 4 — Ressources humaines & Pointage](#9-module-4--ressources-humaines--pointage)
10. [Module 5 — Inscriptions & Paiements (Caissier)](#10-module-5--inscriptions--paiements-caissier)
11. [Module 6 — Vie scolaire (Surveillant)](#11-module-6--vie-scolaire-surveillant)
12. [Module 7 — Espace Professeur](#12-module-7--espace-professeur)
13. [Module 8 — Espace Élève](#13-module-8--espace-élève)
14. [Module 9 — Espace Parent (sans connexion)](#14-module-9--espace-parent-sans-connexion)
15. [Notifications & Messagerie](#15-notifications--messagerie)
16. [Sécurité transversale](#16-sécurité-transversale)
17. [Logging & Audit](#17-logging--audit)
18. [Checklist Pull Request](#18-checklist-pull-request)

---

## 1. Vision produit & roadmap

**Noura School** est une plateforme SaaS B2B permettant à chaque établissement scolaire (collège, lycée) de gérer l'intégralité de sa vie scolaire depuis un seul outil. Chaque établissement est un **tenant isolé**. La plateforme est opérée par nous (propriétaires) via un espace super-admin dédié.

### Roadmap des modules

| Priorité | Module | Statut |
|---|---|---|
| 🔴 P0 | Authentification & Gestion des rôles | 🚧 En cours |
| 🔴 P0 | Gestion plateforme (super admin) | 📋 Planifié |
| 🔴 P0 | Gestion établissement (admin) | 📋 Planifié |
| 🟠 P1 | Inscriptions & Paiements | 📋 Planifié |
| 🟠 P1 | Ressources humaines & Pointage | 📋 Planifié |
| 🟠 P1 | Vie scolaire (emploi du temps, absences) | 📋 Planifié |
| 🟡 P2 | Notes & Bulletins | 📋 Planifié |
| 🟡 P2 | Espace Professeur | 📋 Planifié |
| 🟡 P2 | Espace Élève | 📋 Planifié |
| 🟢 P3 | Espace Parent (liens sécurisés OTP) | 📋 Planifié |
| 🟢 P3 | Notifications WhatsApp / SMS / Mail | 📋 Planifié |
| 🟢 P3 | Examens & Calendrier académique | 📋 Planifié |

---

## 2. Architecture globale

### Stack technique

| Composant | Technologie |
|---|---|
| Langage | Java 21 |
| Framework | Quarkus 3.x |
| Base de données | PostgreSQL 16 |
| ORM | Hibernate ORM + Panache |
| Migrations | Flyway |
| Auth | JWT (RS256) + Refresh Token opaque |
| Notifications | WhatsApp Business API · Twilio SMS · SMTP |
| Stockage fichiers | S3-compatible (documents, bulletins) |
| Cache | Redis (rate limiting, OTP, sessions) |
| Conteneurisation | Docker + Docker Compose |

### Stratégie multi-tenant

Stratégie retenue : **`tenant_id` sur chaque table** (approche schema-per-tenant évaluée ultérieurement si exigences RGPD strictes).

Le `tenantId` est résolu depuis le sous-domaine ou le header `X-Tenant-Id`, injecté dans le contexte CDI `@RequestScoped`, et systématiquement appliqué à toutes les requêtes JPA via un filtre Hibernate.

```
Requête → TenantResolver → JwtAuthFilter → Resource → Service → Repository (filtre tenantId auto)
```

Le claim `tenantId` dans le JWT est validé à chaque requête pour s'assurer qu'il correspond au tenant résolu depuis le header/sous-domaine.

---

## 3. Conventions du projet

### 3.1 Conventions de nommage

| Élément | Convention | Exemple |
|---|---|---|
| Classes Java | PascalCase | `EleveService`, `InscriptionResource` |
| Méthodes / variables | camelCase | `findEleveById`, `montantTotal` |
| Constantes | UPPER_SNAKE_CASE | `MAX_LOGIN_ATTEMPTS` |
| Packages | minuscules | `sn.nouraschool.api.service` |
| Tables SQL | snake_case | `eleve`, `inscription_paiement` |
| Colonnes SQL | snake_case | `date_naissance`, `tenant_id` |
| Fichiers de config | kebab-case | `application-prod.properties` |
| Endpoints REST | kebab-case | `/api/v1/annees-academiques` |
| Enums Java | UPPER_SNAKE_CASE | `COLLEGE`, `EN_ATTENTE` |
| Branches Git | kebab-case | `feature/auth-jwt`, `fix/login-lock` |

### 3.2 Conventions des IDs

- **Tous les IDs sont des UUID v4** — jamais d'auto-incrément exposé en API
- Générés côté serveur uniquement — jamais acceptés depuis le client
- Stockés en `UUID` natif PostgreSQL
- Sérialisés en `String` dans les réponses JSON

```java
// Déclaration standard
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(name = "id", updatable = false, nullable = false)
public UUID id;
```

### 3.3 Conventions des dates

- Toutes les dates sont stockées en **UTC** en base
- Format JSON : **ISO 8601** — `2025-06-01T10:30:00Z`
- Type Java : `Instant` pour les timestamps, `LocalDate` pour les dates simples
- Colonne `created_at` et `updated_at` sur **toutes** les entités
- Soft delete via `deleted_at` — jamais de DELETE physique en production

```java
@Column(name = "created_at", nullable = false, updatable = false)
public Instant createdAt = Instant.now();

@Column(name = "updated_at")
public Instant updatedAt;

@Column(name = "deleted_at")
public Instant deletedAt;
```

### 3.4 Conventions des endpoints REST

- Versionné : `/api/v1/...`
- Ressources en kebab-case pluriel : `/eleves`, `/annees-academiques`
- Pas de verbes dans les URLs — utiliser les méthodes HTTP
- Actions métier en sous-ressource : `POST /eleves/{id}/inscrire`
- Pagination obligatoire sur toutes les listes : `?page=0&size=20`
- Tri : `?sort=nom,asc`
- Filtres : `?cycle=COLLEGE&actif=true`

### 3.5 Conventions de logging

Format obligatoire : `[NomClasse][nomMethode] message {}`

```java
private static final Logger log = LoggerFactory.getLogger(EleveService.class);

// Toujours logger — entrée méthode, sortie, erreurs
log.info("[EleveService][findById] Recherche élève id={}", id);
log.warn("[EleveService][findById] Élève introuvable id={}", id);
log.error("[EleveService][create] Erreur création email={}", email, e);

// INTERDIT
log.info("mot de passe=" + password);   // concaténation + données sensibles
```

### 3.6 Format de réponse standard

**Succès liste :**
```json
{
  "data": [...],
  "page": 0,
  "size": 20,
  "total": 154,
  "totalPages": 8
}
```

**Succès objet :**
```json
{
  "data": { ... }
}
```

**Erreur :**
```json
{
  "code": "ELEVE_INTROUVABLE",
  "message": "L'élève avec l'identifiant fourni est introuvable",
  "details": [],
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2025-06-01T10:30:00Z"
}
```

### 3.7 Codes d'erreur métier

| Code | HTTP | Description |
|---|---|---|
| `IDENTIFIANTS_INVALIDES` | 401 | Login échoué |
| `COMPTE_VERROUILLE` | 423 | Trop de tentatives |
| `TOKEN_EXPIRE` | 401 | JWT expiré |
| `ACCES_REFUSE` | 403 | Rôle insuffisant ou hors périmètre |
| `RESSOURCE_INTROUVABLE` | 404 | Entité non trouvée |
| `EMAIL_DEJA_UTILISE` | 409 | Doublon email |
| `VALIDATION_ECHOUEE` | 400 | Contraintes Bean Validation |
| `REGLE_METIER_VIOLEE` | 422 | Règle métier non respectée |
| `TENANT_INACTIF` | 403 | Établissement suspendu |
| `OTP_INVALIDE` | 401 | Code OTP incorrect ou expiré |

---

## 4. Modèle de données global

### 4.1 Tables plateforme (hors tenant)

```sql
-- Tenant = Établissement scolaire
CREATE TABLE tenant (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug            VARCHAR(100) UNIQUE NOT NULL,
    nom             VARCHAR(255) NOT NULL,
    email_contact   VARCHAR(254),
    telephone       VARCHAR(20),
    adresse         TEXT,
    logo_url        VARCHAR(500),
    plan            VARCHAR(50) NOT NULL DEFAULT 'TRIAL', -- TRIAL | PRO | ENTERPRISE
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    date_expiration DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

-- Comptes plateforme (super admin, gestionnaires Noura School)
CREATE TABLE plateforme_utilisateur (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100) NOT NULL,
    email           VARCHAR(254) UNIQUE NOT NULL,
    mot_de_passe    VARCHAR(255) NOT NULL,
    role_plateforme VARCHAR(50) NOT NULL, -- SUPER_ADMIN | GESTIONNAIRE
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);
```

### 4.2 Tables tenant (toutes portent `tenant_id`)

```sql
-- Utilisateur (tous les rôles sauf parents)
CREATE TABLE utilisateur (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100) NOT NULL,
    email           VARCHAR(254) NOT NULL,
    mot_de_passe    VARCHAR(255),               -- NULL si compte externe (OIDC futur)
    telephone       VARCHAR(20),
    photo_url       VARCHAR(500),
    role            VARCHAR(50) NOT NULL,        -- ADMIN | CAISSIER | SURVEILLANT | PROFESSEUR | ELEVE | RH
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    created_by      UUID REFERENCES utilisateur(id),
    derniere_connexion TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    UNIQUE (email, tenant_id)
);

-- Parent (pas de compte — identifié par email/téléphone)
CREATE TABLE parent (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100) NOT NULL,
    email           VARCHAR(254),
    telephone       VARCHAR(20) NOT NULL,
    whatsapp        VARCHAR(20),
    adresse         TEXT,
    created_by      UUID REFERENCES utilisateur(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ
);

-- Cycle scolaire (COLLEGE, LYCEE)
CREATE TABLE cycle (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    code            VARCHAR(20) NOT NULL,        -- COLLEGE | LYCEE
    libelle         VARCHAR(100) NOT NULL,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, code)
);

-- Niveau scolaire (6ème, 5ème, 2nde, etc.)
CREATE TABLE niveau (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    cycle_id        UUID NOT NULL REFERENCES cycle(id),
    code            VARCHAR(20) NOT NULL,        -- 6EME | 5EME | 2NDE | etc.
    libelle         VARCHAR(100) NOT NULL,
    ordre           INTEGER NOT NULL,            -- pour trier l'affichage
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, cycle_id, code)
);

-- Matière
CREATE TABLE matiere (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    code            VARCHAR(20) NOT NULL,
    libelle         VARCHAR(100) NOT NULL,
    coefficient     DECIMAL(4,2) NOT NULL DEFAULT 1.0,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, code)
);

-- Bâtiment
CREATE TABLE batiment (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    nom             VARCHAR(100) NOT NULL,
    description     TEXT,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Salle
CREATE TABLE salle (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    batiment_id     UUID NOT NULL REFERENCES batiment(id),
    nom             VARCHAR(50) NOT NULL,
    capacite        INTEGER,
    type_salle      VARCHAR(50),                 -- CLASSE | LABO | AMPHI | SALLE_REUNION
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Année académique
CREATE TABLE annee_academique (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    libelle         VARCHAR(20) NOT NULL,         -- ex: 2025-2026
    date_debut      DATE NOT NULL,
    date_fin        DATE NOT NULL,
    est_courante    BOOLEAN NOT NULL DEFAULT FALSE,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, libelle)
);

-- Classe (Niveau + Lettre + Année académique)
CREATE TABLE classe (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    niveau_id       UUID NOT NULL REFERENCES niveau(id),
    annee_academique_id UUID NOT NULL REFERENCES annee_academique(id),
    salle_id        UUID REFERENCES salle(id),
    nom             VARCHAR(50) NOT NULL,         -- ex: 6ème A, Terminale S1
    effectif_max    INTEGER,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, niveau_id, annee_academique_id, nom)
);

-- Elève ↔ Classe (inscription dans une classe)
CREATE TABLE eleve_classe (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    eleve_id        UUID NOT NULL REFERENCES utilisateur(id),
    classe_id       UUID NOT NULL REFERENCES classe(id),
    annee_academique_id UUID NOT NULL REFERENCES annee_academique(id),
    date_inscription DATE NOT NULL,
    statut          VARCHAR(20) NOT NULL DEFAULT 'ACTIF', -- ACTIF | TRANSFERE | EXCLU
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (eleve_id, annee_academique_id)
);

-- Elève ↔ Parent
CREATE TABLE eleve_parent (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    eleve_id        UUID NOT NULL REFERENCES utilisateur(id),
    parent_id       UUID NOT NULL REFERENCES parent(id),
    lien            VARCHAR(50),                 -- PERE | MERE | TUTEUR | AUTRE
    est_contact_principal BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (eleve_id, parent_id)
);

-- Surveillant ↔ Cycle
CREATE TABLE surveillant_cycle (
    surveillant_id  UUID NOT NULL REFERENCES utilisateur(id),
    cycle_id        UUID NOT NULL REFERENCES cycle(id),
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    assigned_by     UUID REFERENCES utilisateur(id),
    PRIMARY KEY (surveillant_id, cycle_id)
);

-- Cours (Matière + Professeur + Classe + Année académique)
CREATE TABLE cours (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    matiere_id      UUID NOT NULL REFERENCES matiere(id),
    professeur_id   UUID NOT NULL REFERENCES utilisateur(id),
    classe_id       UUID NOT NULL REFERENCES classe(id),
    annee_academique_id UUID NOT NULL REFERENCES annee_academique(id),
    volume_horaire_hebdo DECIMAL(4,1),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (matiere_id, professeur_id, classe_id, annee_academique_id)
);

-- Refresh tokens
CREATE TABLE refresh_token (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    utilisateur_id  UUID REFERENCES utilisateur(id),
    plateforme_utilisateur_id UUID REFERENCES plateforme_utilisateur(id),
    token_hash      VARCHAR(255) NOT NULL,
    expires_at      TIMESTAMPTZ NOT NULL,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at      TIMESTAMPTZ,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Audit log
CREATE TABLE audit_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID,
    utilisateur_id  UUID,
    role            VARCHAR(50),
    action          VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(100),
    resource_id     UUID,
    details         JSONB,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 4.3 Index essentiels

```sql
CREATE INDEX idx_utilisateur_tenant        ON utilisateur(tenant_id);
CREATE INDEX idx_utilisateur_email         ON utilisateur(email);
CREATE INDEX idx_utilisateur_role          ON utilisateur(tenant_id, role);
CREATE INDEX idx_parent_tenant             ON parent(tenant_id);
CREATE INDEX idx_parent_telephone          ON parent(tenant_id, telephone);
CREATE INDEX idx_eleve_classe_annee        ON eleve_classe(annee_academique_id);
CREATE INDEX idx_eleve_parent_eleve        ON eleve_parent(eleve_id);
CREATE INDEX idx_cours_classe              ON cours(classe_id, annee_academique_id);
CREATE INDEX idx_refresh_token_hash        ON refresh_token(token_hash);
CREATE INDEX idx_audit_log_tenant          ON audit_log(tenant_id, created_at DESC);
CREATE INDEX idx_audit_log_utilisateur     ON audit_log(utilisateur_id, created_at DESC);
```

---

## 5. Acteurs & rôles — Analyse complète

### Vue hiérarchique

```
SUPER_ADMIN  ──── Plateforme Noura School
GESTIONNAIRE ──── Plateforme Noura School

    │  (par tenant)
    ▼
  ADMIN ──── Directeur d'établissement
    │
    ├── RH            ← Ressources humaines
    ├── CAISSIER      ← Inscriptions & paiements
    ├── SURVEILLANT   ← Vie scolaire (par cycle)
    ├── PROFESSEUR    ← Cours & notes
    └── ELEVE         ← Espace élève
         └── PARENT   ← Pas de compte, liens sécurisés uniquement
```

### Tableau récapitulatif

| Rôle | Scope | Crée par | Connexion |
|---|---|---|---|
| SUPER_ADMIN | Plateforme | Manuel (nous) | ✅ |
| GESTIONNAIRE | Plateforme | SUPER_ADMIN | ✅ |
| ADMIN | Tenant | SUPER_ADMIN ou auto-inscription | ✅ |
| RH | Tenant | ADMIN | ✅ |
| CAISSIER | Tenant | ADMIN ou RH | ✅ |
| SURVEILLANT | Tenant | ADMIN ou RH | ✅ |
| PROFESSEUR | Tenant | ADMIN ou RH | ✅ |
| ELEVE | Tenant | ADMIN, CAISSIER | ✅ |
| PARENT | Tenant | ADMIN, CAISSIER, RH | ❌ (liens OTP) |

---

## 6. Module 1 — Authentification

### Use cases

#### UC-AUTH-01 : Login
- L'utilisateur soumet email + mot de passe
- Vérification bcrypt (cost 12)
- Vérification `actif = true` et `deleted_at IS NULL`
- Si tenant : vérification `tenant.actif = true`
- Génération access token JWT (RS256, 15 min)
- Génération refresh token opaque (UUID, 7 jours, stocké hashé SHA-256)
- Si `must_change_password = true` → flag `passwordChangeRequired: true` dans la réponse
- Enregistrement `audit_log` : action `LOGIN_SUCCESS`
- En cas d'échec : incrémenter compteur tentatives (Redis), enregistrer `LOGIN_FAILURE`
- Après 5 échecs en 10 min → verrouillage compte 30 min

#### UC-AUTH-02 : Refresh token
- Vérification du token opaque (hash en base, non révoqué, non expiré)
- Révocation de l'ancien refresh token
- Si token déjà révoqué présenté → invalider **tous** les refresh tokens de l'utilisateur (détection de vol)
- Génération nouveau access token + nouveau refresh token (rotation stricte)

#### UC-AUTH-03 : Logout
- Révocation du refresh token en base
- L'access token expire naturellement (15 min)
- Enregistrement `audit_log` : action `LOGOUT`

#### UC-AUTH-04 : Mot de passe oublié
- L'utilisateur soumet son email
- Réponse toujours identique (anti-enumeration) : `"Si cet email existe, un lien a été envoyé"`
- Génération token reset (UUID, 1 heure, stocké Redis)
- Envoi email avec lien sécurisé

#### UC-AUTH-05 : Réinitialisation mot de passe
- Validation du token reset (Redis, usage unique)
- Nouveau mot de passe : min 12 caractères, majuscule + chiffre + caractère spécial
- Hash bcrypt, stockage, `must_change_password = false`
- Invalidation de tous les refresh tokens actifs
- Enregistrement `audit_log` : action `PASSWORD_RESET_SUCCESS`

#### UC-AUTH-06 : Changement de mot de passe forcé (première connexion)
- Déclenché si `must_change_password = true`
- L'utilisateur doit changer son mot de passe avant d'accéder à toute autre ressource
- Endpoint dédié : `POST /api/v1/auth/change-password`

#### UC-AUTH-07 : Consultation du profil connecté
- `GET /api/v1/auth/me` — retourne les infos du token courant

### Claims JWT

```json
{
  "sub": "uuid-utilisateur",
  "tenantId": "noura-college-dakar",
  "role": "SURVEILLANT",
  "cycles": ["COLLEGE"],
  "email": "surveillant@ecole.sn",
  "nom": "Diallo",
  "prenom": "Moussa",
  "iat": 1700000000,
  "exp": 1700000900,
  "iss": "https://auth.nouraschool.sn",
  "jti": "uuid-unique-du-token"
}
```

> `cycles` est présent uniquement si `role == SURVEILLANT`.
> `tenantId` est `null` pour SUPER_ADMIN et GESTIONNAIRE.

### Endpoints Auth

```
POST   /api/v1/auth/login                    Login
POST   /api/v1/auth/refresh                  Refresh token
POST   /api/v1/auth/logout                   Logout
POST   /api/v1/auth/forgot-password          Mot de passe oublié
POST   /api/v1/auth/reset-password           Réinitialisation mdp
POST   /api/v1/auth/change-password          Changement forcé (1ère connexion)
GET    /api/v1/auth/me                        Profil connecté
```

### Rate limiting Auth

| Endpoint | Limite | Fenêtre |
|---|---|---|
| `POST /auth/login` | 5 tentatives / IP | 10 min |
| `POST /auth/refresh` | 30 req / token | 1 h |
| `POST /auth/forgot-password` | 3 req / email | 15 min |
| `POST /auth/reset-password` | 3 req / token | 15 min |

---

## 7. Module 2 — Gestion plateforme (Super Admin)

### Acteurs : SUPER_ADMIN, GESTIONNAIRE

### Use cases

#### UC-PLAT-01 : Onboarding d'un établissement
- Création du tenant (slug unique, nom, plan, date d'expiration)
- Création du compte ADMIN initial en transaction atomique
- Envoi email d'accueil avec identifiants temporaires (`must_change_password = true`)
- Enregistrement `audit_log` : action `TENANT_CREATED`

#### UC-PLAT-02 : Auto-inscription d'un établissement
- L'admin remplit un formulaire d'inscription (nom école, email, téléphone)
- Création tenant en statut `TRIAL` (30 jours)
- Création compte ADMIN, envoi mail de confirmation + identifiants
- Validation possible par SUPER_ADMIN avant activation (selon config)

#### UC-PLAT-03 : Gestion des tenants
- Lister tous les tenants avec filtres (plan, actif, date_expiration)
- Voir le détail d'un tenant (stats, utilisateurs, abonnement)
- Modifier les informations d'un tenant
- Suspendre / réactiver un tenant (tous les utilisateurs du tenant sont bloqués)
- Supprimer un tenant (soft delete, conservation des données 90 jours)
- Modifier le plan d'abonnement

#### UC-PLAT-04 : Statistiques plateforme
- Nombre total de tenants actifs / inactifs / trial
- Nombre d'utilisateurs par rôle sur la plateforme
- Nombre de connexions (jour, semaine, mois)
- Évolution des inscriptions par période
- Revenus par tenant / plan

#### UC-PLAT-05 : Gestion des comptes plateforme
- Créer un GESTIONNAIRE (SUPER_ADMIN uniquement)
- Désactiver / réactiver un GESTIONNAIRE
- Un SUPER_ADMIN ne peut pas se désactiver lui-même s'il est le dernier

#### UC-PLAT-06 : Consultation des logs & audit
- Consulter les logs d'audit globaux avec filtres (tenant, action, date, utilisateur)
- Exporter les logs en CSV
- Alertes sur actions suspectes (tentatives de login, accès hors périmètre)

#### UC-PLAT-07 : Gestion de la facturation SaaS
- Voir les paiements reçus par tenant
- Générer une facture
- Envoyer un rappel de renouvellement
- Historique des abonnements

### Endpoints plateforme

```
-- Tenants
POST   /api/platform/tenants                         Créer un tenant          [SUPER_ADMIN]
GET    /api/platform/tenants                         Lister les tenants       [SUPER_ADMIN, GESTIONNAIRE]
GET    /api/platform/tenants/{id}                    Détail tenant            [SUPER_ADMIN, GESTIONNAIRE]
PATCH  /api/platform/tenants/{id}                    Modifier                 [SUPER_ADMIN]
PATCH  /api/platform/tenants/{id}/suspendre          Suspendre                [SUPER_ADMIN]
PATCH  /api/platform/tenants/{id}/reactiver          Réactiver                [SUPER_ADMIN]
DELETE /api/platform/tenants/{id}                    Supprimer (soft)         [SUPER_ADMIN]

-- Comptes plateforme
POST   /api/platform/utilisateurs                    Créer gestionnaire       [SUPER_ADMIN]
GET    /api/platform/utilisateurs                    Lister                   [SUPER_ADMIN]
PATCH  /api/platform/utilisateurs/{id}               Modifier                 [SUPER_ADMIN]
DELETE /api/platform/utilisateurs/{id}               Désactiver               [SUPER_ADMIN]

-- Stats & audit
GET    /api/platform/stats                           Statistiques globales    [SUPER_ADMIN, GESTIONNAIRE]
GET    /api/platform/audit-logs                      Logs d'audit             [SUPER_ADMIN, GESTIONNAIRE]
GET    /api/platform/audit-logs/export               Export CSV               [SUPER_ADMIN]
```

---

## 8. Module 3 — Gestion établissement (Admin)

### Acteurs : ADMIN

### Use cases

#### UC-ADMIN-01 : Configuration de l'établissement
- Modifier le profil de l'établissement (nom, logo, adresse, contacts)
- Paramétrer les cycles actifs (COLLEGE, LYCEE)
- Définir les niveaux par cycle (6ème, 5ème, 4ème, 3ème pour COLLEGE ; 2nde, 1ère, Terminale pour LYCEE)
- Configurer les matières (code, libellé, coefficient)
- Ajouter / modifier / désactiver des bâtiments
- Ajouter / modifier / désactiver des salles (avec bâtiment, capacité, type)

#### UC-ADMIN-02 : Gestion du calendrier académique
- Créer une année académique (libellé, date début/fin)
- Définir les périodes du calendrier : trimestres / semestres
- Définir les périodes d'examens (devoirs, compositions, examens officiels)
- Définir les vacances et jours fériés
- Activer l'année académique courante (une seule à la fois)
- Clôturer une année académique

#### UC-ADMIN-03 : Gestion des classes
- Créer des classes pour une année académique et un niveau (ex: 6ème A, 6ème B)
- Assigner une salle principale à une classe
- Définir l'effectif maximum
- Voir la liste des élèves d'une classe
- Archiver une classe en fin d'année

#### UC-ADMIN-04 : Gestion des utilisateurs
- Créer un compte (CAISSIER, SURVEILLANT, PROFESSEUR, ELEVE, RH)
- Envoi automatique d'un email avec identifiants temporaires
- Modifier les informations d'un utilisateur
- Désactiver / réactiver un compte
- Réinitialiser le mot de passe d'un utilisateur
- Voir l'historique des connexions d'un utilisateur
- Lister tous les utilisateurs avec filtres (rôle, actif, cycle)

#### UC-ADMIN-05 : Assignation des surveillants aux cycles
- Assigner un surveillant à un ou plusieurs cycles
- Retirer un cycle à un surveillant
- Voir les cycles d'un surveillant
- Voir les surveillants d'un cycle

#### UC-ADMIN-06 : Validation des bulletins
- Recevoir une notification quand un surveillant soumet des bulletins pour validation
- Consulter les bulletins avant validation
- Valider les bulletins (ce qui déclenche l'envoi aux parents/élèves)
- Refuser avec commentaire (retour au surveillant)

#### UC-ADMIN-07 : Statistiques établissement
- Effectif total par cycle, niveau, classe
- Taux de présence global
- Taux de réussite par classe / matière / période
- Évolution des paiements

#### UC-ADMIN-08 : Gestion du tableau d'affichage (annonces)
- Publier une annonce visible par les professeurs et/ou élèves
- Programmer une publication (date de début/fin)
- Archiver une annonce

### Endpoints établissement

```
-- Configuration
PATCH  /api/v1/etablissement                         Modifier le profil       [ADMIN]
GET    /api/v1/etablissement                         Voir le profil           [ADMIN]

-- Cycles
POST   /api/v1/cycles                                Créer un cycle           [ADMIN]
GET    /api/v1/cycles                                Lister                   [ADMIN, SURVEILLANT, PROF...]
PATCH  /api/v1/cycles/{id}                           Modifier                 [ADMIN]
DELETE /api/v1/cycles/{id}                           Désactiver               [ADMIN]

-- Niveaux
POST   /api/v1/niveaux                               Créer un niveau          [ADMIN]
GET    /api/v1/niveaux                               Lister                   [ADMIN]
GET    /api/v1/niveaux?cycleId={id}                  Filtrer par cycle        [ADMIN]
PATCH  /api/v1/niveaux/{id}                          Modifier                 [ADMIN]
DELETE /api/v1/niveaux/{id}                          Désactiver               [ADMIN]

-- Matières
POST   /api/v1/matieres                              Créer                    [ADMIN]
GET    /api/v1/matieres                              Lister                   [ADMIN, SURVEILLANT]
PATCH  /api/v1/matieres/{id}                         Modifier                 [ADMIN]
DELETE /api/v1/matieres/{id}                         Désactiver               [ADMIN]

-- Bâtiments & Salles
POST   /api/v1/batiments                             Créer un bâtiment        [ADMIN]
GET    /api/v1/batiments                             Lister                   [ADMIN]
POST   /api/v1/batiments/{id}/salles                 Créer une salle          [ADMIN]
GET    /api/v1/batiments/{id}/salles                 Lister les salles        [ADMIN]
PATCH  /api/v1/salles/{id}                           Modifier une salle       [ADMIN]

-- Années académiques
POST   /api/v1/annees-academiques                    Créer                    [ADMIN]
GET    /api/v1/annees-academiques                    Lister                   [ADMIN]
GET    /api/v1/annees-academiques/courante           Année en cours           [ALL]
PATCH  /api/v1/annees-academiques/{id}               Modifier                 [ADMIN]
POST   /api/v1/annees-academiques/{id}/activer       Activer                  [ADMIN]
POST   /api/v1/annees-academiques/{id}/clotures      Clôturer                 [ADMIN]

-- Calendrier académique
POST   /api/v1/annees-academiques/{id}/periodes      Créer une période        [ADMIN]
GET    /api/v1/annees-academiques/{id}/periodes       Lister les périodes      [ALL]
PATCH  /api/v1/periodes/{id}                         Modifier                 [ADMIN]
DELETE /api/v1/periodes/{id}                         Supprimer                [ADMIN]

-- Classes
POST   /api/v1/classes                               Créer une classe         [ADMIN]
GET    /api/v1/classes                               Lister                   [ADMIN, SURVEILLANT]
GET    /api/v1/classes/{id}                          Détail                   [ADMIN, SURVEILLANT, PROF]
GET    /api/v1/classes/{id}/eleves                   Élèves d'une classe      [ADMIN, SURVEILLANT, PROF]
PATCH  /api/v1/classes/{id}                          Modifier                 [ADMIN]

-- Utilisateurs
POST   /api/v1/utilisateurs                          Créer                    [ADMIN, RH]
GET    /api/v1/utilisateurs                          Lister                   [ADMIN, RH]
GET    /api/v1/utilisateurs/{id}                     Détail                   [ADMIN, RH]
PATCH  /api/v1/utilisateurs/{id}                     Modifier                 [ADMIN, RH]
DELETE /api/v1/utilisateurs/{id}                     Désactiver               [ADMIN]
POST   /api/v1/utilisateurs/{id}/reinitialiser-mdp   Reset mdp                [ADMIN, RH]
GET    /api/v1/utilisateurs/{id}/connexions          Historique connexions    [ADMIN]

-- Assignation cycles surveillants
POST   /api/v1/utilisateurs/{id}/cycles              Assigner cycles          [ADMIN]
DELETE /api/v1/utilisateurs/{id}/cycles/{cycleId}    Retirer cycle            [ADMIN]

-- Cours (Matière + Professeur + Classe)
POST   /api/v1/cours                                 Créer un cours           [ADMIN, SURVEILLANT]
GET    /api/v1/cours                                 Lister                   [ADMIN, SURVEILLANT]
GET    /api/v1/cours?classeId={id}                   Cours d'une classe       [ADMIN, SURVEILLANT, PROF]
PATCH  /api/v1/cours/{id}                            Modifier                 [ADMIN, SURVEILLANT]
DELETE /api/v1/cours/{id}                            Supprimer                [ADMIN]

-- Annonces
POST   /api/v1/annonces                              Créer                    [ADMIN]
GET    /api/v1/annonces                              Lister                   [ADMIN, PROF, ELEVE]
PATCH  /api/v1/annonces/{id}                         Modifier                 [ADMIN]
DELETE /api/v1/annonces/{id}                         Supprimer                [ADMIN]

-- Stats
GET    /api/v1/stats/etablissement                   Statistiques générales   [ADMIN]
```

---

## 9. Module 4 — Ressources humaines & Pointage

### Acteurs : RH (création par ADMIN)

### Use cases

#### UC-RH-01 : Gestion du personnel
- Créer des fiches personnelles pour les membres du staff (caissier, professeur, surveillant)
- Gérer les informations contractuelles (type contrat, date embauche, salaire)
- Suivre les congés et absences du personnel
- Archiver un dossier de personnel

#### UC-RH-02 : Système de pointage
- Enregistrer les entrées et sorties du personnel (timestamp + type : ENTREE | SORTIE)
- Peut se faire via QR code, code PIN ou saisie manuelle
- Génération de rapports de présence par période
- Alertes sur les retards et absences récurrents

#### UC-RH-03 : Gestion des absences du personnel
- Déclarer une absence (avec motif, pièce justificative)
- Valider ou refuser une absence
- Suivre les soldes de congés
- Rapport mensuel d'absences

#### UC-RH-04 : Gestion du staff (même périmètre que ADMIN pour les comptes)
- Créer des comptes CAISSIER, SURVEILLANT, PROFESSEUR
- Modifier les informations des comptes staff
- Ne peut pas créer un ADMIN ou un autre RH

### Tables supplémentaires

```sql
-- Fiche personnel
CREATE TABLE personnel (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    utilisateur_id  UUID NOT NULL REFERENCES utilisateur(id),
    numero_matricule VARCHAR(50),
    type_contrat    VARCHAR(50),     -- CDI | CDD | VACATAIRE
    date_embauche   DATE,
    salaire         DECIMAL(10,2),
    solde_conge     INTEGER DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

-- Pointage
CREATE TABLE pointage (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    utilisateur_id  UUID NOT NULL REFERENCES utilisateur(id),
    type_pointage   VARCHAR(10) NOT NULL,   -- ENTREE | SORTIE
    date_heure      TIMESTAMPTZ NOT NULL,
    methode         VARCHAR(20),            -- QR_CODE | CODE_PIN | MANUEL
    created_by      UUID REFERENCES utilisateur(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Absence personnel
CREATE TABLE absence_personnel (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    utilisateur_id  UUID NOT NULL REFERENCES utilisateur(id),
    date_debut      DATE NOT NULL,
    date_fin        DATE NOT NULL,
    motif           VARCHAR(255),
    type_absence    VARCHAR(50),            -- MALADIE | CONGE | AUTRE
    justificatif_url VARCHAR(500),
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE', -- EN_ATTENTE | VALIDEE | REFUSEE
    valide_par      UUID REFERENCES utilisateur(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Endpoints RH

```
-- Personnel
POST   /api/v1/personnel                             Créer une fiche          [ADMIN, RH]
GET    /api/v1/personnel                             Lister                   [ADMIN, RH]
GET    /api/v1/personnel/{id}                        Détail                   [ADMIN, RH]
PATCH  /api/v1/personnel/{id}                        Modifier                 [ADMIN, RH]

-- Pointage
POST   /api/v1/pointages                             Enregistrer              [ADMIN, RH, AUTO]
GET    /api/v1/pointages                             Lister avec filtres      [ADMIN, RH]
GET    /api/v1/pointages/rapport                     Rapport présence         [ADMIN, RH]

-- Absences personnel
POST   /api/v1/absences-personnel                    Déclarer absence         [ADMIN, RH, USER concerné]
GET    /api/v1/absences-personnel                    Lister                   [ADMIN, RH]
PATCH  /api/v1/absences-personnel/{id}/valider       Valider                  [ADMIN, RH]
PATCH  /api/v1/absences-personnel/{id}/refuser       Refuser                  [ADMIN, RH]
```

---

## 10. Module 5 — Inscriptions & Paiements (Caissier)

### Acteurs : CAISSIER (peut aussi être fait par ADMIN)

### Use cases

#### UC-CAIS-01 : Inscription d'un élève
- Créer le profil de l'élève (nom, prénom, date naissance, photo)
- Créer ou retrouver le/les parent(s) (max 2 par élève)
- Affecter l'élève à une classe de l'année académique courante
- Vérifier que la classe n'a pas atteint son effectif maximum
- Générer un numéro d'inscription unique
- Créer une fiche de paiement selon les frais de la classe

#### UC-CAIS-02 : Gestion des parents
- Créer un parent (nom, prénom, email, téléphone, whatsapp)
- Rechercher un parent existant (par téléphone ou email) pour éviter les doublons
- Lier un parent à un élève avec le type de lien (père, mère, tuteur)
- Un élève peut avoir 1 ou 2 parents liés

#### UC-CAIS-03 : Gestion des paiements
- Enregistrer un paiement pour une inscription
- Générer un reçu PDF avec numéro unique
- Consulter l'historique des paiements d'un élève
- Gérer les paiements en plusieurs échéances
- Tableau de bord des encaissements du jour / mois
- Solde restant dû par élève

#### UC-CAIS-04 : Paiements mensuels parents (liens sécurisés)
- Génération automatique de liens de paiement sécurisés chaque fin de mois
- Envoi WhatsApp / SMS au parent avec le lien et le montant dû
- Le parent ouvre le lien → OTP envoyé sur son téléphone → accès au récapitulatif et paiement
- Suivi des paiements reçus vs attendus

#### UC-CAIS-05 : Transfert d'élève
- Transférer un élève d'une classe à une autre (même année académique)
- Historique des transferts

### Tables supplémentaires

```sql
-- Inscription
CREATE TABLE inscription (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           UUID NOT NULL,
    numero_inscription  VARCHAR(50) UNIQUE NOT NULL,
    eleve_id            UUID NOT NULL REFERENCES utilisateur(id),
    classe_id           UUID NOT NULL REFERENCES classe(id),
    annee_academique_id UUID NOT NULL REFERENCES annee_academique(id),
    statut              VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    cree_par            UUID NOT NULL REFERENCES utilisateur(id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Paiement
CREATE TABLE paiement (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    inscription_id  UUID NOT NULL REFERENCES inscription(id),
    numero_recu     VARCHAR(50) UNIQUE NOT NULL,
    montant         DECIMAL(10,2) NOT NULL,
    montant_total   DECIMAL(10,2) NOT NULL,
    solde_restant   DECIMAL(10,2) NOT NULL,
    mode_paiement   VARCHAR(30) NOT NULL,  -- ESPECES | VIREMENT | MOBILE_MONEY | CHEQUE
    statut          VARCHAR(20) NOT NULL DEFAULT 'VALIDE',
    encaisse_par    UUID NOT NULL REFERENCES utilisateur(id),
    date_paiement   DATE NOT NULL,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Lien paiement sécurisé parent
CREATE TABLE lien_paiement_parent (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    parent_id       UUID NOT NULL REFERENCES parent(id),
    token           VARCHAR(255) UNIQUE NOT NULL,   -- token opaque
    montant_total   DECIMAL(10,2) NOT NULL,
    mois            VARCHAR(7) NOT NULL,            -- ex: 2025-06
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    expires_at      TIMESTAMPTZ NOT NULL,
    otp_hash        VARCHAR(255),
    otp_expires_at  TIMESTAMPTZ,
    otp_verified    BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Endpoints Inscriptions & Paiements

```
-- Élèves
POST   /api/v1/eleves                                Créer un élève           [ADMIN, CAISSIER]
GET    /api/v1/eleves                                Lister                   [ADMIN, CAISSIER, SURVEILLANT*]
GET    /api/v1/eleves/{id}                           Détail                   [ADMIN, CAISSIER, SURVEILLANT*, PROF*]
PATCH  /api/v1/eleves/{id}                           Modifier                 [ADMIN, CAISSIER]
GET    /api/v1/eleves/{id}/parents                   Parents d'un élève       [ADMIN, CAISSIER, SURVEILLANT*]
GET    /api/v1/eleves/{id}/paiements                 Historique paiements     [ADMIN, CAISSIER]

-- Parents
POST   /api/v1/parents                               Créer                    [ADMIN, CAISSIER]
GET    /api/v1/parents                               Lister                   [ADMIN, CAISSIER]
GET    /api/v1/parents/{id}                          Détail                   [ADMIN, CAISSIER]
PATCH  /api/v1/parents/{id}                          Modifier                 [ADMIN, CAISSIER]
POST   /api/v1/parents/recherche                     Rechercher (tel/email)   [ADMIN, CAISSIER]
POST   /api/v1/parents/{id}/eleves/{eleveId}         Lier à un élève          [ADMIN, CAISSIER]
DELETE /api/v1/parents/{id}/eleves/{eleveId}         Délier                   [ADMIN]

-- Inscriptions
POST   /api/v1/inscriptions                          Inscrire un élève        [ADMIN, CAISSIER]
GET    /api/v1/inscriptions                          Lister                   [ADMIN, CAISSIER]
GET    /api/v1/inscriptions/{id}                     Détail                   [ADMIN, CAISSIER]
PATCH  /api/v1/inscriptions/{id}/transferer          Transfert de classe      [ADMIN, CAISSIER]

-- Paiements
POST   /api/v1/paiements                             Enregistrer paiement     [ADMIN, CAISSIER]
GET    /api/v1/paiements                             Lister                   [ADMIN, CAISSIER]
GET    /api/v1/paiements/{id}/recu                   Générer reçu PDF         [ADMIN, CAISSIER]
GET    /api/v1/paiements/rapport-journalier          Rapport du jour          [ADMIN, CAISSIER]

-- Liens paiement parent (OTP)
POST   /api/v1/liens-paiement/generer                Générer liens du mois    [ADMIN, CAISSIER]
POST   /api/v1/liens-paiement/{token}/verifier-otp   Vérifier OTP             [PUBLIC]
GET    /api/v1/liens-paiement/{token}/detail          Voir détail paiement     [PUBLIC + OTP vérifié]
POST   /api/v1/liens-paiement/{token}/payer           Enregistrer paiement     [PUBLIC + OTP vérifié]
```

---

## 11. Module 6 — Vie scolaire (Surveillant)

### Acteurs : SURVEILLANT (périmètre = ses cycles assignés)

### Use cases

#### UC-SURV-01 : Emploi du temps
- Créer les créneaux horaires de l'emploi du temps par classe
- Assigner cours + professeur + salle + jour + heure
- Vérifier les conflits (même professeur, même salle, même horaire)
- Publier l'emploi du temps (visible par professeurs et élèves)
- Modifier / republier un emploi du temps

#### UC-SURV-02 : Organisation des examens et devoirs
- Planifier les sessions d'examens dans le calendrier académique
- Créer une session d'examen (type, dates, classes concernées)
- Créer les sujets d'examen par cours
- Publier le planning d'examens (visible professeurs + élèves)

#### UC-SURV-03 : Gestion des absences élèves
- Recevoir les listes d'absence soumises par les professeurs
- Approuver ou corriger une liste d'absence
- À l'approbation → notification automatique WhatsApp au(x) parent(s)
- Consulter le récapitulatif des absences par élève / classe / période
- Générer le rapport d'absences mensuel
- Enregistrer une absence manuellement (hors cours)

#### UC-SURV-04 : Gestion des notes & bulletins
- Voir les notes saisies par les professeurs par cours
- Valider les notes d'une période pour une classe
- Déclencher le calcul des bulletins (moyennes par matière, moyenne générale, rang)
- Vérifier les bulletins générés
- Soumettre les bulletins à l'ADMIN pour validation finale
- Après validation ADMIN → envoi automatique aux parents et élèves

#### UC-SURV-05 : Suivi pédagogique des professeurs
- Voir le cahier de texte rempli par chaque professeur
- Suivre l'avancement du programme par cours
- Voir le taux de présence d'un professeur

#### UC-SURV-06 : Convocation des parents
- Créer une convocation pour un parent (motif, date, heure)
- Envoi via WhatsApp / SMS automatique
- Enregistrer le compte rendu de la convocation

#### UC-SURV-07 : Partage de documents / informations
- Publier un document ou une information à destination des professeurs et/ou élèves
- Ciblage : tous les élèves d'un cycle, d'une classe, ou nominatif

### Tables supplémentaires

```sql
-- Créneau emploi du temps
CREATE TABLE emploi_du_temps (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    cours_id        UUID NOT NULL REFERENCES cours(id),
    salle_id        UUID REFERENCES salle(id),
    jour_semaine    INTEGER NOT NULL,   -- 1=Lundi ... 6=Samedi
    heure_debut     TIME NOT NULL,
    heure_fin       TIME NOT NULL,
    annee_academique_id UUID NOT NULL REFERENCES annee_academique(id),
    publie          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Absence élève
CREATE TABLE absence_eleve (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    eleve_id        UUID NOT NULL REFERENCES utilisateur(id),
    cours_id        UUID REFERENCES cours(id),
    date_absence    DATE NOT NULL,
    heure_debut     TIME,
    heure_fin       TIME,
    motif           TEXT,
    justifiee       BOOLEAN NOT NULL DEFAULT FALSE,
    justificatif_url VARCHAR(500),
    notifie_parent  BOOLEAN NOT NULL DEFAULT FALSE,
    soumis_par      UUID REFERENCES utilisateur(id),   -- professeur
    approuve_par    UUID REFERENCES utilisateur(id),   -- surveillant
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Note
CREATE TABLE note (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    eleve_id        UUID NOT NULL REFERENCES utilisateur(id),
    cours_id        UUID NOT NULL REFERENCES cours(id),
    periode_id      UUID NOT NULL,   -- période du calendrier académique
    type_evaluation VARCHAR(50) NOT NULL,  -- DEVOIR | COMPOSITION | EXAMEN
    valeur          DECIMAL(5,2) NOT NULL,
    note_sur        DECIMAL(5,2) NOT NULL DEFAULT 20.0,
    commentaire     TEXT,
    saisie_par      UUID NOT NULL REFERENCES utilisateur(id),
    validee_par     UUID REFERENCES utilisateur(id),
    statut          VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Bulletin
CREATE TABLE bulletin (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    eleve_id        UUID NOT NULL REFERENCES utilisateur(id),
    classe_id       UUID NOT NULL REFERENCES classe(id),
    periode_id      UUID NOT NULL,
    moyenne_generale DECIMAL(5,2),
    rang            INTEGER,
    appreciation    TEXT,
    statut          VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    soumis_par      UUID REFERENCES utilisateur(id),
    valide_par      UUID REFERENCES utilisateur(id),
    envoye_le       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (eleve_id, periode_id)
);

-- Lien bulletin sécurisé parent
CREATE TABLE lien_bulletin_parent (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    bulletin_id     UUID NOT NULL REFERENCES bulletin(id),
    parent_id       UUID NOT NULL REFERENCES parent(id),
    token           VARCHAR(255) UNIQUE NOT NULL,
    otp_hash        VARCHAR(255),
    otp_expires_at  TIMESTAMPTZ,
    otp_verified    BOOLEAN DEFAULT FALSE,
    expires_at      TIMESTAMPTZ NOT NULL,
    ouvert_le       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Convocation parent
CREATE TABLE convocation (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    parent_id       UUID NOT NULL REFERENCES parent(id),
    eleve_id        UUID NOT NULL REFERENCES utilisateur(id),
    motif           TEXT NOT NULL,
    date_convocation TIMESTAMPTZ NOT NULL,
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    compte_rendu    TEXT,
    cree_par        UUID NOT NULL REFERENCES utilisateur(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Endpoints Vie scolaire

```
-- Emploi du temps
POST   /api/v1/emplois-du-temps                      Créer un créneau         [SURVEILLANT, ADMIN]
GET    /api/v1/emplois-du-temps?classeId={id}        Par classe               [SURVEILLANT, PROF, ELEVE]
PATCH  /api/v1/emplois-du-temps/{id}                 Modifier                 [SURVEILLANT, ADMIN]
DELETE /api/v1/emplois-du-temps/{id}                 Supprimer                [SURVEILLANT, ADMIN]
POST   /api/v1/emplois-du-temps/publier?classeId={id} Publier                  [SURVEILLANT, ADMIN]

-- Absences élèves
GET    /api/v1/absences-eleves                       Lister                   [SURVEILLANT, ADMIN]
GET    /api/v1/absences-eleves?eleveId={id}          Par élève                [SURVEILLANT, PROF, ELEVE*]
POST   /api/v1/absences-eleves/{id}/approuver        Approuver                [SURVEILLANT]
POST   /api/v1/absences-eleves/{id}/rejeter          Rejeter                  [SURVEILLANT]
POST   /api/v1/absences-eleves/rapport               Rapport absences         [SURVEILLANT, ADMIN]

-- Notes
GET    /api/v1/notes?classeId={id}&periodeId={id}    Notes d'une classe       [SURVEILLANT, ADMIN]
POST   /api/v1/notes/valider-periode                 Valider une période      [SURVEILLANT]

-- Bulletins
POST   /api/v1/bulletins/generer                     Générer les bulletins    [SURVEILLANT]
GET    /api/v1/bulletins?classeId={id}&periodeId={id} Lister                   [SURVEILLANT, ADMIN]
GET    /api/v1/bulletins/{id}                        Détail                   [SURVEILLANT, ADMIN, ELEVE*]
POST   /api/v1/bulletins/soumettre?periodeId={id}    Soumettre à l'ADMIN      [SURVEILLANT]
POST   /api/v1/bulletins/valider?periodeId={id}      Valider et envoyer       [ADMIN]
GET    /api/v1/bulletins/{token}/consulter           Consulter (lien OTP)     [PUBLIC + OTP]
POST   /api/v1/bulletins/{token}/verifier-otp        Vérifier OTP             [PUBLIC]

-- Convocations
POST   /api/v1/convocations                          Créer convocation        [SURVEILLANT, ADMIN]
GET    /api/v1/convocations                          Lister                   [SURVEILLANT, ADMIN]
PATCH  /api/v1/convocations/{id}/compte-rendu        Compte rendu             [SURVEILLANT, ADMIN]

-- Partage documents
POST   /api/v1/documents                             Partager un document     [SURVEILLANT, ADMIN]
GET    /api/v1/documents                             Lister                   [PROF, ELEVE, SURVEILLANT, ADMIN]
DELETE /api/v1/documents/{id}                        Supprimer                [SURVEILLANT, ADMIN]
```

---

## 12. Module 7 — Espace Professeur

### Use cases

#### UC-PROF-01 : Appel & émargement
- Effectuer l'appel pour un cours (sélectionner la liste présent/absent/retard)
- Soumettre la liste d'appel → passe en statut EN_ATTENTE pour validation du surveillant
- Consulter l'historique de ses appels

#### UC-PROF-02 : Saisie des notes
- Saisir les notes pour ses cours et classes
- Saisie par type d'évaluation (devoir, composition, examen)
- Modifier une note avant validation du surveillant
- Voir les notes saisies (lecture seule après validation)

#### UC-PROF-03 : Cahier de texte numérique
- Remplir le cahier de texte après chaque cours (contenu traité, observations)
- Décrire son programme prévisionnel pour une période
- Valider une étape du programme après chaque cours

#### UC-PROF-04 : Partage de documents
- Uploader et partager des documents avec ses classes
- Les élèves voient uniquement les documents de leurs classes
- Voir les documents partagés avec lui

#### UC-PROF-05 : Notifications à ses classes
- Envoyer une notification texte à une ou plusieurs de ses classes
- Notification reçue par les élèves dans leur espace

#### UC-PROF-06 : Consultation de son emploi du temps
- Voir son emploi du temps hebdomadaire

### Tables supplémentaires

```sql
-- Cahier de texte
CREATE TABLE cahier_texte (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    cours_id        UUID NOT NULL REFERENCES cours(id),
    date_cours      DATE NOT NULL,
    contenu_traite  TEXT,
    observations    TEXT,
    etape_programme VARCHAR(255),
    programme_valide BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

-- Appel (émargement)
CREATE TABLE appel (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    cours_id        UUID NOT NULL REFERENCES cours(id),
    date_cours      DATE NOT NULL,
    heure_debut     TIME,
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    soumis_par      UUID NOT NULL REFERENCES utilisateur(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Ligne d'appel
CREATE TABLE appel_ligne (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    appel_id        UUID NOT NULL REFERENCES appel(id),
    eleve_id        UUID NOT NULL REFERENCES utilisateur(id),
    statut          VARCHAR(20) NOT NULL,   -- PRESENT | ABSENT | RETARD
    PRIMARY KEY (appel_id, eleve_id)
);
```

### Endpoints Professeur

```
-- Appel
POST   /api/v1/appels                                Créer un appel           [PROF]
GET    /api/v1/appels?coursId={id}                   Historique appels        [PROF, SURVEILLANT]
PATCH  /api/v1/appels/{id}/soumettre                 Soumettre                [PROF]

-- Notes
POST   /api/v1/notes                                 Saisir une note          [PROF]
GET    /api/v1/notes?coursId={id}&periodeId={id}     Mes notes par cours      [PROF]
PATCH  /api/v1/notes/{id}                            Modifier (avant valid.)  [PROF]

-- Cahier de texte
POST   /api/v1/cahier-texte                          Créer une entrée         [PROF]
GET    /api/v1/cahier-texte?coursId={id}             Historique               [PROF, SURVEILLANT]
PATCH  /api/v1/cahier-texte/{id}                     Modifier                 [PROF]

-- Emploi du temps prof
GET    /api/v1/mon-emploi-du-temps                   Mon EDT                  [PROF]

-- Documents
POST   /api/v1/documents                             Partager doc             [PROF, SURVEILLANT]
GET    /api/v1/documents/mes-documents               Mes documents partagés   [PROF]
```

---

## 13. Module 8 — Espace Élève

### Use cases

#### UC-ELEVE-01 : Tableau de bord
- Voir ses informations personnelles
- Voir la classe et l'année académique courante

#### UC-ELEVE-02 : Emploi du temps
- Consulter son emploi du temps de la semaine courante

#### UC-ELEVE-03 : Notes et bulletins
- Voir ses notes par matière et par période
- Voir ses bulletins (après validation et envoi)

#### UC-ELEVE-04 : Absences
- Voir l'historique de ses absences
- Notifier une absence à venir (reçue par le surveillant et les parents)
- Joindre un justificatif

#### UC-ELEVE-05 : Documents et notifications
- Consulter les documents partagés par ses professeurs et la direction
- Recevoir et consulter les notifications (changements EDT, annonces, etc.)

#### UC-ELEVE-06 : Réclamation de notes
- Faire une réclamation sur une note avec justificatif
- Suivre le statut de sa réclamation

### Tables supplémentaires

```sql
-- Réclamation de note
CREATE TABLE reclamation_note (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    note_id         UUID NOT NULL REFERENCES note(id),
    eleve_id        UUID NOT NULL REFERENCES utilisateur(id),
    motif           TEXT NOT NULL,
    justificatif_url VARCHAR(500),
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    reponse         TEXT,
    traite_par      UUID REFERENCES utilisateur(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Notification
CREATE TABLE notification (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    destinataire_id UUID NOT NULL REFERENCES utilisateur(id),
    titre           VARCHAR(255) NOT NULL,
    contenu         TEXT,
    type            VARCHAR(50),   -- INFO | ABSENCE | NOTE | BULLETIN | CONVOCATION
    lu              BOOLEAN NOT NULL DEFAULT FALSE,
    lu_le           TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Endpoints Élève

```
GET    /api/v1/mon-profil                            Mon profil               [ELEVE]
GET    /api/v1/mon-emploi-du-temps                   Mon EDT                  [ELEVE]
GET    /api/v1/mes-notes                             Mes notes                [ELEVE]
GET    /api/v1/mes-bulletins                         Mes bulletins            [ELEVE]
GET    /api/v1/mes-absences                          Mes absences             [ELEVE]
POST   /api/v1/mes-absences/notifier                 Notifier absence         [ELEVE]
GET    /api/v1/mes-documents                         Mes documents            [ELEVE]
GET    /api/v1/mes-notifications                     Mes notifications        [ELEVE]
PATCH  /api/v1/mes-notifications/{id}/lire           Marquer comme lue        [ELEVE]
POST   /api/v1/reclamations-notes                    Créer réclamation        [ELEVE]
GET    /api/v1/reclamations-notes                    Mes réclamations         [ELEVE]
```

---

## 14. Module 9 — Espace Parent (sans connexion)

Le parent n'a pas de compte sur la plateforme. Tous les accès passent par des **liens sécurisés avec OTP**.

### Use cases

#### UC-PAR-01 : Consultation du bulletin (lien sécurisé)
- Après validation de l'ADMIN, un lien unique est généré par bulletin par parent
- Envoi WhatsApp ou email avec le lien
- Le parent ouvre le lien → saisit son numéro → reçoit OTP (SMS/WhatsApp) → consulte le bulletin
- Le lien expire après 30 jours ou 5 consultations

#### UC-PAR-02 : Paiement mensuel (lien sécurisé)
- Chaque fin de mois, lien de paiement généré automatiquement
- Le parent reçoit un message WhatsApp avec le montant dû et le lien
- Authentification OTP → récapitulatif des frais → paiement (Mobile Money, etc.)

#### UC-PAR-03 : Notifications automatiques
- **Absence de l'élève** : message WhatsApp dès approbation de l'absence par le surveillant
- **Convocation** : message WhatsApp avec date, heure et motif
- **Bulletin disponible** : message WhatsApp avec lien sécurisé
- **Retard de paiement** : rappel mensuel automatique

### Endpoints Parent (publics, protégés par OTP)

```
POST   /api/v1/parent/bulletins/{token}/demander-otp   Demander OTP          [PUBLIC]
POST   /api/v1/parent/bulletins/{token}/verifier-otp   Vérifier OTP          [PUBLIC]
GET    /api/v1/parent/bulletins/{token}                Consulter bulletin     [PUBLIC + OTP]

POST   /api/v1/parent/paiements/{token}/demander-otp   Demander OTP          [PUBLIC]
POST   /api/v1/parent/paiements/{token}/verifier-otp   Vérifier OTP          [PUBLIC]
GET    /api/v1/parent/paiements/{token}                Voir récapitulatif     [PUBLIC + OTP]
POST   /api/v1/parent/paiements/{token}/payer          Payer                  [PUBLIC + OTP]
```

---

## 15. Notifications & Messagerie

### Canaux supportés

| Canal | Usage | Lib / API |
|---|---|---|
| WhatsApp | Absences, bulletins, convocations, paiements | WhatsApp Business API |
| SMS | OTP, fallback WhatsApp | Twilio / Orange API |
| Email | Création de compte, reset mdp, bulletins | SMTP / SendGrid |
| In-app | Notifications internes (élèves, professeurs) | WebSocket ou polling |

### Use cases de notification

| Déclencheur | Canal | Destinataire |
|---|---|---|
| Compte créé | Email | Utilisateur (avec identifiants temporaires) |
| Absence approuvée | WhatsApp | Parent(s) de l'élève |
| Bulletin validé | WhatsApp + Email | Parent(s) + Élève |
| Convocation créée | WhatsApp | Parent |
| Paiement fin de mois | WhatsApp | Parent |
| Retard paiement | WhatsApp | Parent |
| Élève notifie absence | In-app | Surveillant + Parent |
| Document partagé | In-app | Classes ciblées |
| Annonce publiée | In-app | Cibles définies |
| Reset mot de passe | Email | Utilisateur |

### Table notifications

```sql
CREATE TABLE notification_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID,
    canal           VARCHAR(20) NOT NULL,   -- WHATSAPP | SMS | EMAIL | IN_APP
    destinataire    VARCHAR(254),           -- email ou téléphone
    sujet           VARCHAR(255),
    contenu         TEXT,
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    erreur          TEXT,
    envoye_le       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## 16. Sécurité transversale

### Règles générales

- HTTPS obligatoire — aucune route HTTP en production
- TLS 1.2 minimum
- Algorithme JWT : **RS256** (clé asymétrique)
- Mots de passe : **bcrypt cost 12**
- OTP : 6 chiffres, valide **5 minutes**, usage unique, max 3 tentatives
- Tous les fichiers uploadés sont stockés sur S3, jamais servis directement — URLs pré-signées temporaires (15 min)

### Isolation tenant (obligatoire partout)

```java
// Filtre automatique via Hibernate @Filter
// Chaque repository doit appliquer le filtre tenant
// Jamais de requête sans vérification tenant_id
```

### Headers de sécurité

```properties
quarkus.http.header."Strict-Transport-Security".value=max-age=31536000; includeSubDomains
quarkus.http.header."X-Frame-Options".value=DENY
quarkus.http.header."X-Content-Type-Options".value=nosniff
quarkus.http.header."Referrer-Policy".value=strict-origin-when-cross-origin
quarkus.http.header."Content-Security-Policy".value=default-src 'none'; frame-ancestors 'none'
quarkus.http.header."Permissions-Policy".value=geolocation=(), microphone=()
```

### Ce qu'on ne logue JAMAIS

- Mots de passe (même hashés)
- Tokens JWT ou refresh tokens
- Codes OTP
- Numéros de carte bancaire
- Données personnelles complètes (seulement IDs dans les logs)

---

## 17. Logging & Audit

### Format de log obligatoire

```
[NomClasse][nomMethode] message {}
```

```java
// Déclaration dans chaque classe
private static final Logger log = LoggerFactory.getLogger(MaClasse.class);

// Exemples corrects
log.info("[EleveService][create] Création élève tenantId={} classeId={}", tenantId, classeId);
log.warn("[AuthService][login] Tentative échouée email={} attempt={}", email, attempts);
log.error("[BulletinService][generer] Erreur génération periodeId={}", periodeId, e);
```

### Actions auditées (audit_log)

| Action | Déclencheur |
|---|---|
| `LOGIN_SUCCESS` / `LOGIN_FAILURE` | Auth |
| `LOGOUT` | Auth |
| `TOKEN_REFRESH` | Auth |
| `PASSWORD_RESET` | Auth |
| `TENANT_CREATED` / `TENANT_SUSPENDED` | Plateforme |
| `USER_CREATED` / `USER_DEACTIVATED` | Admin |
| `INSCRIPTION_CREATED` | Caissier |
| `PAIEMENT_ENREGISTRE` | Caissier |
| `BULLETIN_VALIDE` / `BULLETIN_ENVOYE` | Admin / Surveillant |
| `ABSENCE_APPROUVEE` | Surveillant |
| `NOTE_VALIDEE` | Surveillant |
| `OTP_VERIFIE` / `OTP_ECHEC` | Parent / lien public |
| `DOCUMENT_PARTAGE` | Surveillant / Prof |

---

## 18. Checklist Pull Request

### Sécurité

- [ ] Tous les endpoints ont `@Authenticated` + `@RolesAllowed`
- [ ] Le `tenantId` est systématiquement vérifié (jamais de données cross-tenant)
- [ ] Aucun secret en dur dans le code
- [ ] Les inputs sont validés avec `@Valid`
- [ ] Pas de stack trace dans les réponses HTTP
- [ ] Aucune donnée sensible dans les logs

### Logging

- [ ] Logger déclaré dans chaque classe : `LoggerFactory.getLogger(MaClasse.class)`
- [ ] Format respecté : `[NomClasse][nomMethode] message {}`
- [ ] Chaque méthode de service logue son entrée, sa sortie et ses erreurs
- [ ] Pas de concaténation dans les appels de log
- [ ] Actions sensibles enregistrées dans `audit_log`

### Architecture & Code

- [ ] Tests unitaires ajoutés (couverture ≥ 80%)
- [ ] Tests `@TestSecurity` pour chaque endpoint (auth + RBAC + hors périmètre)
- [ ] Migration Flyway versionnée correctement (`V{n}__{description}.sql`)
- [ ] DTOs distincts des entités JPA
- [ ] Records Java 21 pour les DTOs immuables
- [ ] Listes paginées (max 50 éléments par défaut)
- [ ] Pas de `TODO` / `FIXME` en production

### Notifications

- [ ] Toute notification est enregistrée dans `notification_log` avant envoi
- [ ] Les envois sont asynchrones (ne pas bloquer la réponse HTTP)
- [ ] Les échecs d'envoi sont tracés et retentés

---

*Noura School — Document de référence backend v1.0*
*Java 21 · Quarkus 3.x · Toute modification via PR sur `main` avec review obligatoire*