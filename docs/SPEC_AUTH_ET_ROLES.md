# NouraSchool — Système de Gestion Scolaire

> **Java 21 · Quarkus 3.x · REST · JPA · JWT / OIDC**
> Plateforme SaaS multi-tenant pour la gestion d'établissements scolaires (collège & lycée).
> **Périmètre actuel : Authentification & Gestion des rôles.**

---

## Table des matières

1. [Vision produit & roadmap](#1-vision-produit--roadmap)
2. [Architecture multi-tenant](#2-architecture-multi-tenant)
3. [Cycles scolaires supportés](#3-cycles-scolaires-supportés)
4. [Acteurs & rôles](#4-acteurs--rôles)
5. [Modèle de données — Auth & Rôles](#5-modèle-de-données--auth--rôles)
6. [Authentification — Analyse détaillée](#6-authentification--analyse-détaillée)
7. [Gestion des rôles — Analyse détaillée](#7-gestion-des-rôles--analyse-détaillée)
8. [Endpoints — Auth & Rôles](#8-endpoints--auth--rôles)
9. [Matrice des permissions](#9-matrice-des-permissions)
10. [Sécurité & contraintes techniques](#10-sécurité--contraintes-techniques)
11. [Décisions d'architecture](#11-décisions-darchitecture)

---

## 1. Vision produit & roadmap

NouraSchool est une plateforme **SaaS multi-tenant** permettant à chaque établissement scolaire de disposer de son propre espace isolé, géré par son administration. L'objectif est de centraliser l'ensemble de la vie scolaire sur une seule plateforme.

### Modules prévus

| Statut | Module |
|---|---|
| 🚧 **En cours** | Authentification & Gestion des rôles |
| 📋 Planifié | Gestion des inscriptions & des élèves |
| 📋 Planifié | Gestion des absences |
| 📋 Planifié | Gestion des notes & bulletins |
| 📋 Planifié | Emplois du temps |
| 📋 Planifié | Examens |
| 📋 Planifié | Calendrier scolaire |
| 📋 Planifié | Paiements & facturation |

### Contraintes SaaS structurantes

Toutes les décisions prises aujourd'hui sur l'auth et les rôles doivent anticiper ces contraintes :

- **Multi-tenant** : chaque école est un tenant isolé. Un utilisateur n'existe que dans le contexte d'un tenant. Un compte super-admin gère la plateforme globalement.
- **Isolation des données** : un utilisateur d'un établissement A ne peut jamais accéder aux données de l'établissement B.
- **Rôles contextuels** : un même utilisateur peut avoir des rôles différents selon le tenant.
- **Onboarding** : l'inscription d'un nouvel établissement crée automatiquement un tenant et un compte ADMIN initial.
- **Scalabilité** : le modèle de rôles doit pouvoir s'étendre sans refonte (ajout de permissions fines par module).

---

## 2. Architecture multi-tenant

### Stratégie retenue : tenant par schéma / colonne tenant_id

Chaque établissement dispose d'un identifiant tenant. Le `tenantId` est résolu à partir du sous-domaine ou d'un header HTTP, puis injecté dans le contexte de la requête.

```
Requête entrante
      │
      ▼
┌─────────────────────┐
│  TenantResolver     │  Résout le tenant depuis :
│  (CDI RequestScope) │  - Sous-domaine : ecole-victor-hugo.nouraschool.com
│                     │  - Header       : X-Tenant-Id: ecole-victor-hugo
└─────────┬───────────┘
          │ tenantId injecté dans MDC + contexte CDI
          ▼
┌─────────────────────┐
│  JwtAuthFilter      │  Valide le JWT
│                     │  Vérifie que le claim tenantId du token
│                     │  correspond au tenant de la requête
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│  Resource / Service │  Logique métier
│                     │  Toutes les requêtes JPA filtrent
│                     │  automatiquement par tenantId
└─────────────────────┘
```

### Claims JWT obligatoires

```json
{
  "sub": "uuid-de-l-utilisateur",
  "tenantId": "ecole-victor-hugo",
  "role": "ADMIN",
  "email": "directeur@ecole-victor-hugo.fr",
  "iat": 1700000000,
  "exp": 1700000900,
  "iss": "https://auth.nouraschool.com"
}
```

---

## 3. Cycles scolaires supportés

Pour cette version, seuls deux cycles sont gérés. L'architecture doit permettre d'ajouter `PRIMAIRE` et d'autres cycles sans refonte.

| Enum | Libellé | Niveaux couverts |
|---|---|---|
| `COLLEGE` | Collège | 6ème, 5ème, 4ème, 3ème |
| `LYCEE` | Lycée | 2nde, 1ère, Terminale |

Le cycle est un attribut central : il conditionne les permissions des surveillants, l'organisation des classes, et sera la clé de répartition pour les emplois du temps et les examens dans les modules futurs.

---

## 4. Acteurs & rôles

### Vue globale

```
SUPER_ADMIN  (plateforme)
└── gère les tenants / établissements

ADMIN  (établissement)
├── crée et gère tous les comptes de l'établissement
└── configure les paramètres de l'établissement

CAISSIER  (établissement)
└── inscriptions : crée élèves + parents

SURVEILLANT  (établissement)
└── périmètre limité à ses cycles assignés

PROFESSEUR  (établissement)
└── accès lecture sur ses classes (futur)

PARENT  (établissement)
└── accès lecture sur ses enfants uniquement
```

---

### SUPER_ADMIN

Rôle plateforme, hors tenant. N'intervient pas dans la vie scolaire d'un établissement.

**Responsabilités :** Créer/suspendre/supprimer un tenant, créer le compte ADMIN initial (onboarding), métriques plateforme, facturation SaaS.

**Contrainte :** Ne peut pas accéder aux données métier d'un tenant sauf support tracé.

---

### ADMIN

Rôle le plus haut au sein d'un établissement. Au minimum un ADMIN par tenant.

**Responsabilités :** Créer/modifier/désactiver tous les comptes du tenant, assigner les cycles aux surveillants, valider les inscriptions, configurer l'établissement, accès à tous les modules du tenant.

**Contrainte :** Un ADMIN ne peut agir que dans son tenant. Ne peut pas se désactiver lui-même s'il est le dernier ADMIN actif.

---

### CAISSIER

**Responsabilités :** Inscrire de nouveaux élèves (création élève + parent/tuteur), rattacher un élève à un parent existant, consulter les inscriptions traitées. Futur : module Paiement.

**Contrainte :** Ne peut pas créer de comptes staff. Ne voit pas les données des autres caissiers.

---

### SURVEILLANT

**Responsabilités :** Consulter les élèves et parents de ses cycles assignés. Futur : absences, emplois du temps de ses cycles.

**Contrainte :** Le token JWT embarque la liste de ses cycles. Toute requête sur un cycle non assigné → `403`.

---

### PROFESSEUR

**Responsabilités :** Futur : saisir les notes, gérer les absences de ses cours, consulter son emploi du temps.

**Contrainte :** Accès limité à ses classes et matières.

---

### PARENT

**Responsabilités :** Consulter le profil et les infos de ses enfants rattachés. Futur : notes, absences, emploi du temps, paiements.

**Contrainte :** Ne voit que ses propres données. Accès à un enfant non rattaché → `403`. *Option : accès via OTP (numéro) pour partage bulletins, etc.*

---

## 5. Modèle de données — Auth & Rôles

### Entités

```sql
-- Tenant (établissement)
CREATE TABLE tenant (
    id              UUID PRIMARY KEY,
    slug            VARCHAR(100) UNIQUE NOT NULL,
    nom             VARCHAR(255) NOT NULL,
    actif           BOOLEAN DEFAULT TRUE,
    plan            VARCHAR(50),
    created_at      TIMESTAMP NOT NULL,
    deleted_at      TIMESTAMP
);

-- Utilisateur
CREATE TABLE utilisateur (
    id              UUID PRIMARY KEY,
    tenant_id       UUID REFERENCES tenant(id),     -- NULL pour SUPER_ADMIN
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100) NOT NULL,
    email           VARCHAR(254) NOT NULL,
    mot_de_passe    VARCHAR(255) NOT NULL,
    role            VARCHAR(50) NOT NULL,
    actif           BOOLEAN DEFAULT TRUE,
    created_by      UUID REFERENCES utilisateur(id),
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP,
    deleted_at      TIMESTAMP,
    UNIQUE (email, tenant_id)
);

-- Surveillant → cycles assignés
CREATE TABLE surveillant_cycle (
    surveillant_id  UUID REFERENCES utilisateur(id),
    cycle           VARCHAR(20) NOT NULL,            -- COLLEGE | LYCEE
    assigned_at     TIMESTAMP NOT NULL,
    assigned_by     UUID REFERENCES utilisateur(id),
    PRIMARY KEY (surveillant_id, cycle)
);

-- Refresh tokens
CREATE TABLE refresh_token (
    id              UUID PRIMARY KEY,
    utilisateur_id  UUID REFERENCES utilisateur(id),
    token_hash      VARCHAR(255) NOT NULL,
    expires_at      TIMESTAMP NOT NULL,
    revoked         BOOLEAN DEFAULT FALSE,
    revoked_at      TIMESTAMP,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMP NOT NULL
);

-- Audit log
CREATE TABLE audit_log (
    id              UUID PRIMARY KEY,
    tenant_id       UUID,
    utilisateur_id  UUID,
    action          VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(100),
    resource_id     UUID,
    details         JSONB,
    ip_address      VARCHAR(45),
    created_at      TIMESTAMP NOT NULL
);
```

### Index

```sql
CREATE INDEX idx_utilisateur_tenant    ON utilisateur(tenant_id);
CREATE INDEX idx_utilisateur_email     ON utilisateur(email);
CREATE INDEX idx_refresh_token_hash    ON refresh_token(token_hash);
CREATE INDEX idx_refresh_token_user    ON refresh_token(utilisateur_id);
CREATE INDEX idx_audit_log_tenant      ON audit_log(tenant_id);
CREATE INDEX idx_audit_log_user        ON audit_log(utilisateur_id);
```

---

## 6. Authentification — Analyse détaillée

### Stratégie

- **JWT stateless** pour les access tokens.
- **Refresh token opaque** stocké en base (révocable, rotation à chaque usage).
- **Bcrypt** pour le hashage des mots de passe (cost 12 minimum).
- **HTTPS obligatoire**.

### Cycle de vie des tokens

- **Login** : vérification email + mot de passe, compte actif + tenant actif → Access Token (JWT, 15 min) + Refresh Token (opaque, 7 jours).
- **Refresh** : valide le token opaque, révoque l'ancien → nouvel Access Token + nouveau Refresh Token (rotation stricte).
- **Logout** : révocation du refresh token en base.

### Règles de sécurité

- Verrouillage du compte après **5 tentatives échouées** en 10 minutes (délai exponentiel).
- Message d'erreur de login toujours générique : `"Identifiants invalides"`.
- Un refresh token ne peut être utilisé qu'**une seule fois** (rotation).
- Si un refresh token déjà révoqué est présenté → invalider tous les refresh tokens de l'utilisateur (détection de vol).
- Stocker IP et User-Agent du refresh token pour audit *(prévu, non implémenté)*.

### Mot de passe

- Longueur minimum : **12 caractères** ; majuscule, minuscule, chiffre, caractère spécial.
- Hashé **bcrypt** (cost 12).
- Réinitialisation via token à usage unique (lien email, valide 1 heure).

---

## 7. Gestion des rôles — Analyse détaillée

### Règles de création de comptes

| Créateur | Peut créer |
|---|---|
| SUPER_ADMIN | ADMIN (premier compte d'un tenant) |
| ADMIN | CAISSIER, SURVEILLANT, PROFESSEUR, PARENT (et élèves dans le cadre inscriptions) |
| CAISSIER | PARENT, ÉLÈVE (dans le cadre d'une inscription uniquement) |
| SURVEILLANT | Aucun |
| PROFESSEUR | Aucun |
| PARENT | Aucun |

**Règles complémentaires :**

- Un ADMIN ne peut pas créer un SUPER_ADMIN.
- Le dernier ADMIN actif d'un tenant ne peut pas être désactivé.
- Désactivation = **soft delete** (`deleted_at` + `actif = false`).

### Assignation de cycles (SURVEILLANT)

- Cycles assignés par un ADMIN via table `surveillant_cycle`.
- Le claim `cycles` du JWT est recalculé à chaque login/refresh.
- Retirer un cycle → effectif au prochain token (ou invalider le refresh pour révocation immédiate).

---

## 8. Endpoints — Auth & Rôles

### Auth

```
POST   /api/v1/auth/login
POST   /api/v1/auth/refresh
POST   /api/v1/auth/logout
POST   /api/v1/auth/forgot-password
POST   /api/v1/auth/reset-password
GET    /api/v1/auth/me
```

**Login (body)** : `{ "login": "...", "password": "..." }` — `login` accepte email, username ou téléphone.  
**Réponse** : `accessToken`, `refreshToken`, `expiresIn`, `passwordChangeRequired`.

**Refresh (body)** : `{ "refreshToken": "..." }` → même format de réponse que login.

**Logout** : Header `Authorization: Bearer <accessToken>` — révocation de tous les refresh tokens de l'utilisateur.

**GET /me** : Header `Authorization` → profil courant (id, nom, prenom, email, role, tenantId, **cycles** si SURVEILLANT, actif).

### Gestion des utilisateurs (ADMIN)

```
POST   /api/v1/utilisateurs                         [ADMIN]
GET    /api/v1/utilisateurs                         [ADMIN]
GET    /api/v1/utilisateurs/{id}                    [ADMIN]
PATCH  /api/v1/utilisateurs/{id}                    [ADMIN]
DELETE /api/v1/utilisateurs/{id}                    [ADMIN]  (soft delete)
PATCH  /api/v1/utilisateurs/{id}/reinitialiser-mdp  [ADMIN]
```

Pour `POST /utilisateurs` : body avec `nom`, `prenom`, `email`, `motDePasse`, `role`. Si `role == SURVEILLANT`, `cycles` requis (ex. `["COLLEGE", "LYCEE"]`).

### Gestion des cycles (SURVEILLANT)

```
POST   /api/v1/utilisateurs/{id}/cycles             [ADMIN]
DELETE /api/v1/utilisateurs/{id}/cycles/{cycle}    [ADMIN]
GET    /api/v1/utilisateurs/{id}/cycles             [ADMIN]
```

### Gestion des tenants (SUPER_ADMIN / GESTIONNAIRE)

```
POST   /api/platform/tenants                        [GESTIONNAIRE, SUPER_ADMIN]
GET    /api/platform/tenants                        [GESTIONNAIRE, SUPER_ADMIN]
GET    /api/platform/tenants/{id}                   [GESTIONNAIRE, SUPER_ADMIN]
PATCH  /api/platform/tenants/{id}                   [GESTIONNAIRE, SUPER_ADMIN]
POST   /api/platform/tenants/{id}/suspendre         [SUPER_ADMIN]
POST   /api/platform/tenants/{id}/reactiver         [SUPER_ADMIN]
```

**POST /tenants** (onboarding) : body avec `slug`, `nom`, `plan`, `emailContact`, etc. → crée tenant + premier ADMIN en une transaction.

---

## 9. Matrice des permissions

| Action | SUPER_ADMIN | ADMIN | CAISSIER | SURVEILLANT | PROFESSEUR | PARENT |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| Login | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Refresh token | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Voir son profil (`/me`) | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Créer un tenant | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Créer ADMIN | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Créer CAISSIER / SURVEILLANT / PROFESSEUR / PARENT | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Créer PARENT / ÉLÈVE (inscription) | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ |
| Lister / désactiver utilisateurs | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Assigner cycles (surveillant) | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Suspendre un tenant | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

---

## 10. Sécurité & contraintes techniques

- **JWT** : RS256, issuer configurable, access token 15 min, refresh 7 jours.
- **Headers** : HSTS, X-Frame-Options, X-Content-Type-Options, Referrer-Policy, CSP.
- **Rate limiting** : login 5/10 min par IP ; refresh 20/h par token ; forgot-password 3/15 min par email.
- **Audit** : tracer dans `audit_log` : LOGIN_SUCCESS/FAILURE, LOGOUT, TOKEN_REFRESH, CREATE_USER, UPDATE_USER, DEACTIVATE_USER, ASSIGN_CYCLE, REMOVE_CYCLE, PASSWORD_RESET_*, TENANT_*.

---

## 11. Décisions d'architecture

- **Rôles fixes** pour l’instant ; permissions granulaires envisagées plus tard.
- **Refresh token opaque** pour révocation et rotation stricte.
- **Cycles dans le JWT** du surveillant pour éviter un appel base à chaque requête.
- **Multi-tenant** : colonne `tenant_id` (ou schéma par tenant si besoin d’isolation renforcée).

---

## 12. État d'implémentation

| Élément | Implémenté | Détail |
|---------|:----------:|--------|
| Login / refresh / logout / forgot / reset / change-password / me | ✅ | Corps login : `{ "login", "password" }` |
| Verrouillage 5 tentatives / 10 min | ✅ | Redis, délai exponentiel |
| Rotation refresh token + détection vol | ✅ | |
| Cycles dans `/me` (SURVEILLANT) | ✅ | Liste des codes cycles assignés |
| Protection dernier ADMIN | ✅ | Suppression + désactivation (actif=false) interdites |
| Mot de passe 12 caractères min | ✅ | reset, change, reinitialiserMdp |
| Table users (vs utilisateur) | ✅ | Schéma actuel : `users` |
| Rôle ENSEIGNANT (vs PROFESSEUR) | ✅ | Enum actuel : ENSEIGNANT |
| TenantResolver | ✅ | Header `X-Tenant-Id` (UUID) uniquement |
| Tenants API | ✅ | `/api/platform/tenants`, suspend/reactivate en POST |
| IP/User-Agent sur refresh_tokens | ⏳ | Prévu, non implémenté |
| Reinitialiser-mdp | ✅ | `POST /utilisateurs/{id}/reinitialiser-mdp` |
| Cycles surveillant | ✅ | `POST/DELETE /utilisateurs/{id}/cycles`, `surveillant_cycle` avec `cycle_id` (UUID) |

---

*NouraSchool — v1.0 · Module Auth & Rôles · Java 21 · Quarkus 3.x*
