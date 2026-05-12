# Noura School — Documentation globale

> **Document unique** pour comprendre le système, intégrer l'API frontend, et savoir ce qui reste à faire.
> Destiné aux **développeurs frontend**, aux **product owners** et à toute personne devant comprendre le fonctionnement.

---

## Table des matières

1. [Pour les non-développeurs : comment ça marche ?](#1-pour-les-non-développeurs--comment-ça-marche-)
2. [Vue d'ensemble technique](#2-vue-densemble-technique)
3. [Authentification (à intégrer en premier)](#3-authentification-à-intégrer-en-premier)
4. [Structure des API par rôle](#4-structure-des-api-par-rôle)
5. [Répertoire complet des endpoints](#5-répertoire-complet-des-endpoints)
6. [Format des réponses et codes d'erreur](#6-format-des-réponses-et-codes-derreur)
7. [Conventions et normes](#7-conventions-et-normes)
8. [Ce qui reste à faire / à fournir](#8-ce-qui-reste-à-faire--à-fournir)

---

## 1. Pour les non-développeurs : comment ça marche ?

### Qu'est-ce que Noura School ?

**Noura School** est une plateforme de gestion scolaire pour collèges et lycées. Chaque établissement a son propre espace (on parle de **tenant**). Les données d'un établissement ne sont jamais visibles par un autre.

### Qui utilise le système ?

| Rôle | Qui c'est | Ce qu'il peut faire |
|------|-----------|---------------------|
| **ADMIN** | Directeur, responsable d'établissement | Crée les comptes (caissiers, surveillants, enseignants, élèves, parents), gère les classes, matières, notes, bulletins, emplois du temps, absences, calendrier, etc. |
| **CAISSIER** | Comptable, secrétaire | Inscrit les élèves, crée les parents, gère les paiements et leur validation |
| **RH** | Responsable des ressources humaines | Gère le personnel, les pointages et les absences du personnel |
| **SURVEILLANT** | CPE, surveillant général | Voit les élèves et absences de ses cycles assignés (ex. collège uniquement ou lycée uniquement), gère les convocations |
| **ENSEIGNANT** | Professeur | Voit ses classes et matières, saisit les notes, fait les appels, le cahier de texte |
| **ELEVE** | Élève | Voit son profil, ses notes, bulletins validés, absences, emploi du temps, peut déposer des réclamations |
| **PARENT** | Parent d'élève | **N'accède pas à la plateforme.** Reçoit des liens par email/SMS (bulletin, paiement, absences, emploi du temps, convocations, etc.) et consulte le contenu via ces liens (souvent avec OTP) |

### Flux typique

1. **L'admin** crée le tenant (établissement) ou c'est fait à l'inscription.
2. **L'admin** crée les comptes : caissiers, surveillants, enseignants.
3. **L'admin ou le caissier** inscrit les élèves et crée les comptes parents.
4. **L'enseignant** saisit les notes, fait les appels, gère le cahier de texte.
5. **L'élève** consulte ses notes et bulletins.
6. **Le parent** reçoit des liens (par email/SMS) pour consulter bulletin, paiement, absences, emploi du temps, convocations, etc. — pas d'espace de connexion.

### Multi-tenant

Chaque école = **1 tenant**. Les données sont isolées : une école A ne voit jamais les données d'une école B.

### Ce qui est prêt aujourd'hui

- ✅ Connexion / déconnexion / mot de passe oublié
- ✅ Gestion des utilisateurs (admin crée caissiers, surveillants, enseignants, élèves, parents)
- ✅ Cycles, niveaux, bâtiments, salles, années académiques, cours
- ✅ Inscriptions, paiements, liens de paiement OTP
- ✅ Notes, bulletins, absences élèves, convocations, emplois du temps
- ✅ Espace professeur (appels, cahier de texte, notes)
- ✅ Espace élève (profil, notes, bulletins, absences, réclamations, notifications)
- ✅ Personnel, pointage, absences personnel
- ✅ Liens bulletin parent (génération, consultation OTP)
- ✅ Gestion plateforme (tenants, super-admin)
- ✅ Envoi d'emails (compte créé, reset mdp, bulletin), OTP par email

---

## 2. Vue d'ensemble technique

### Stack

- **Backend** : Java 21, Quarkus 3.x, PostgreSQL, Redis
- **API** : REST JSON, préfixe `/api/`
- **Auth** : JWT (15 min) + refresh token (7 jours)
- **Multi-tenant** : header `X-Tenant-Id` (UUID de l'établissement)

### Organisation des routes

| Préfixe | Qui | Description |
|---------|-----|-------------|
| `/api/v1/auth` | Tous (public ou connecté) | Connexion, déconnexion, mot de passe |
| `/api/v1/` | Selon ressource | Cycles, niveaux, inscriptions, paiements, etc. |
| `/api/admin/` | ADMIN | Élèves, parents, classes, matières, notes, bulletins, absences, etc. |
| `/api/enseignant/` | ENSEIGNANT | Espace professeur |
| `/api/eleve/` | ELEVE | Espace élève |
| Liens parent | Public (token) | Parent : bulletin, paiement, absences, EDT, convocations… (pas de connexion) |
| `/api/caisse/` | CAISSIER | Paiements, validation |
| `/api/v1/personnel`, `/api/v1/pointages`, `/api/v1/absences-personnel` | ADMIN, RH | Personnel, pointage, absences personnel |
| `/api/platform/` | SUPER_ADMIN, GESTIONNAIRE | Tenants, stats, audit |

### Base URL

- **Développement** : `http://localhost:8080`
- **Production** : à définir (ex. `https://api.noura-school.com`)

### Documentation OpenAPI (Swagger)

En développement : **`GET /q/swagger-ui`** — liste interactive de tous les endpoints.

---

## 3. Authentification (à intégrer en premier)

### Connexion (login)

**POST** `/api/v1/auth/login`

**Headers** : `Content-Type: application/json`

**Body** :
```json
{
  "login": "admin",
  "password": "MotDePasse123!"
}
```

- `login` : email, username ou numéro de téléphone
- `password` : mot de passe (minimum 12 caractères en création/réinit)
- Pour un compte plateforme (`SUPER_ADMIN` ou `GESTIONNAIRE`), `login` est l'email de `plateforme_utilisateur`.

**Réponse 200** :
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "uuid-opaque",
  "expiresIn": 900,
  "passwordChangeRequired": false
}
```

- `accessToken` : JWT à envoyer dans `Authorization: Bearer <accessToken>`
- `refreshToken` : pour obtenir un nouveau JWT sans reconnecter
- `expiresIn` : durée de validité en secondes (900 = 15 min)
- `passwordChangeRequired` : `true` si l'utilisateur doit changer son mot de passe

Pour un compte plateforme, `refreshToken` peut être `null`; le token contient `accountType=PLATFORM` et un rôle `SUPER_ADMIN` ou `GESTIONNAIRE`.

### Rafraîchir le token (refresh)

**POST** `/api/v1/auth/refresh`

**Body** :
```json
{
  "refreshToken": "uuid-opaque-reçu-au-login"
}
```

**Réponse** : même format que le login (nouvel `accessToken`, nouveau `refreshToken`).

### Profil connecté (me)

**GET** `/api/v1/auth/me`

**Headers** : `Authorization: Bearer <accessToken>`

**Réponse 200** :
```json
{
  "id": "uuid",
  "nom": "Dupont",
  "prenom": "Marie",
  "email": "marie@ecole.fr",
  "role": "ENSEIGNANT",
  "tenantId": "uuid-tenant",
  "cycles": ["COLLEGE"],
  "actif": true
}
```

- `cycles` : présent uniquement si `role == SURVEILLANT` (cycles assignés : COLLEGE, LYCEE)

### Déconnexion (logout)

**POST** `/api/v1/auth/logout`

**Headers** : `Authorization: Bearer <accessToken>`

**Réponse** : 204 No Content. Tous les refresh tokens de l'utilisateur sont révoqués.

### Mot de passe oublié

**POST** `/api/v1/auth/forgot-password`

**Body** :
```json
{
  "email": "user@ecole.fr"
}
```

**Réponse** : 204. Un email est envoyé avec un lien contenant un token (valide 1 h). Pas de détection d'existence (sécurité).

### Réinitialisation (avec token reçu par email)

**POST** `/api/v1/auth/reset-password`

**Body** :
```json
{
  "token": "token-du-lien-email",
  "newPassword": "NouveauMotDePasse123!"
}
```

**Réponse** : 204. Le mot de passe est mis à jour, tous les refresh tokens sont révoqués.

### Changement de mot de passe (utilisateur connecté)

**POST** `/api/v1/auth/change-password`

**Headers** : `Authorization: Bearer <accessToken>`

**Body** :
```json
{
  "oldPassword": "Ancien123!",
  "newPassword": "Nouveau123!"
}
```

### Header obligatoire pour les appels métier

Après login, **chaque requête** (sauf login, refresh, forgot-password, reset-password) doit inclure :

```
Authorization: Bearer <accessToken>
```

Et pour le multi-tenant (sauf SUPER_ADMIN) :

```
X-Tenant-Id: <uuid-du-tenant>
```

Le `tenantId` est disponible dans la réponse de `/me`. Si le JWT contient un `tenantId`, le backend vérifie que `X-Tenant-Id` correspond au tenant du token.

### Erreurs auth fréquentes

| Code | Signification | Action frontend |
|------|---------------|-----------------|
| `IDENTIFIANTS_INVALIDES` | Login ou mot de passe incorrect | Afficher message générique, pas "email inconnu" |
| `COMPTE_VERROUILLE` | 5 tentatives échouées en 10 min | Afficher délai avant nouvel essai |
| `USER_INACTIVE` | Compte désactivé | Message "Compte désactivé" |
| `TENANT_INACTIF` | Établissement suspendu | Message adapté |
| `REFRESH_TOKEN_INVALID` | Refresh token invalide ou révoqué | Rediriger vers écran login |
| `MOT_DE_PASSE_TROP_COURT` | Moins de 12 caractères | Demander 12 caractères minimum |

---

## 4. Structure des API par rôle

### ADMIN — `/api/admin/*` et `/api/v1/*`

- **Utilisateurs** : `/api/v1/utilisateurs` — CRUD, réinitialiser MDP, assigner cycles
- **Admin users** : `/api/admin/users` — CRUD (caissiers, surveillants)
- **Élèves** : `/api/admin/eleves`
- **Parents** : `/api/admin/parents`
- **Enseignants** : `/api/admin/enseignants`
- **Classes** : `/api/admin/classes`
- **Matières** : `/api/admin/matieres`
- **Matières-classes** : `/api/admin/matieres-classes`
- **Emplois du temps** : `/api/admin/emplois-du-temps`
- **Absences élèves** : `/api/admin/absences-eleves`
- **Notes** : `/api/admin/notes`
- **Bulletins** : `/api/admin/bulletins`
- **Réclamations** : `/api/admin/reclamations`
- **Paiements** : `/api/admin/paiements`
- **Calendrier** : `/api/admin/calendrier-scolaire`
- **Rapports** : `/api/admin/reports/rapport-trimestre`

### CAISSIER — `/api/caisse/*`

- Paiements : liste, par statut, historique, valider

### RH — Ressources humaines (ADMIN ou RH)

- **Personnel** : `/api/v1/personnel` — CRUD personnel
- **Pointages** : `/api/v1/pointages` — créer, lister, rapport
- **Absences personnel** : `/api/v1/absences-personnel` — créer, valider, refuser

### ENSEIGNANT — `/api/enseignant/*`

- Profil, classes-matieres, emploi du temps
- Notes (lire, créer, modifier)
- Bulletins, absences, réclamations
- Appels (créer, soumettre)
- Cahier de texte (créer, modifier)

### ELEVE — `/api/eleve/*`

- Profil, notes, bulletins (validés uniquement)
- Emploi du temps, absences
- Réclamations (créer, lister)
- Notifications (lister, marquer comme lues)

### PARENT — accès par liens uniquement (pas de connexion)

Le parent **n'a pas d'espace de connexion**. Il reçoit des liens par email ou SMS et ouvre directement le contenu (souvent après vérification OTP). Types de liens :

- **Bulletins** : `/api/v1/liens-bulletin/{token}/consulter` — consulter un bulletin
- **Paiements** : `/api/v1/liens-paiement/{token}/detail`, `payer` — payer une facture
- **Absences** : lien pour consulter les absences d'un enfant (à prévoir)
- **Emploi du temps** : lien pour consulter l'EDT d'un enfant (à prévoir)
- **Convocations** : lien pour consulter une convocation (à prévoir)

### Plateforme (SUPER_ADMIN / GESTIONNAIRE) — `/api/platform/*`

- Tenants : CRUD, suspend, reactivate, auto-register
- Utilisateurs plateforme
- Stats, audit logs

---

## 5. Répertoire complet des endpoints

### Auth (public ou connecté)

| Méthode | URL | Rôle | Description |
|---------|-----|------|-------------|
| POST | `/api/v1/auth/login` | Public | Connexion |
| POST | `/api/v1/auth/refresh` | Public | Rafraîchir le token |
| GET | `/api/v1/auth/me` | Connecté | Profil courant |
| POST | `/api/v1/auth/logout` | Connecté | Déconnexion |
| POST | `/api/v1/auth/forgot-password` | Public | Mot de passe oublié |
| POST | `/api/v1/auth/reset-password` | Public | Réinit avec token |
| POST | `/api/v1/auth/change-password` | Connecté | Changer MDP |

### Établissement (v1)

| Méthode | URL | Rôle | Description |
|---------|-----|------|-------------|
| GET/POST | `/api/v1/cycles` | ADMIN | Cycles (COLLEGE, LYCEE) |
| GET/POST/PUT/DELETE | `/api/v1/cycles/{id}` | ADMIN | CRUD cycle |
| GET/POST | `/api/v1/niveaux` | ADMIN | Niveaux |
| GET/POST/PUT/DELETE | `/api/v1/niveaux/{id}` | ADMIN | CRUD niveau |
| GET/POST | `/api/v1/batiments` | ADMIN | Bâtiments |
| GET/POST/PUT/DELETE | `/api/v1/batiments/{id}` | ADMIN | CRUD bâtiment |
| GET | `/api/v1/batiments/{id}/salles` | ADMIN | Salles d'un bâtiment |
| GET/POST | `/api/v1/salles` | ADMIN | Salles |
| GET/POST/PUT/DELETE | `/api/v1/salles/{id}` | ADMIN | CRUD salle |
| GET/POST | `/api/v1/annees-academiques` | ADMIN | Années académiques |
| GET | `/api/v1/annees-academiques/courante` | ADMIN | Année courante |
| GET/POST/PUT/DELETE | `/api/v1/annees-academiques/{id}` | ADMIN | CRUD année |
| POST | `/api/v1/annees-academiques/{id}/activer` | ADMIN | Activer une année |
| GET/POST | `/api/v1/cours` | ADMIN | Cours |
| GET/POST/PUT/DELETE | `/api/v1/cours/{id}` | ADMIN | CRUD cours |
| GET | `/api/v1/stats/etablissement` | ADMIN | Statistiques établissement |
| GET/POST/PATCH/DELETE | `/api/v1/annonces` | ADMIN | Annonces |

### Utilisateurs (v1)

| Méthode | URL | Rôle | Description |
|---------|-----|------|-------------|
| GET/POST | `/api/v1/utilisateurs` | ADMIN | Liste, création |
| GET/PATCH/DELETE | `/api/v1/utilisateurs/{id}` | ADMIN | Détail, modification, suppression |
| POST | `/api/v1/utilisateurs/{id}/reinitialiser-mdp` | ADMIN | Forcer réinit MDP |
| POST | `/api/v1/utilisateurs/{id}/cycles` | ADMIN | Assigner cycles (SURVEILLANT) |
| DELETE | `/api/v1/utilisateurs/{id}/cycles/{cycleId}` | ADMIN | Retirer un cycle |

### Admin — Élèves, parents, enseignants, etc.

| Méthode | URL | Description |
|---------|-----|-------------|
| GET/POST | `/api/admin/eleves` | CRUD élèves |
| GET/PUT/DELETE | `/api/admin/eleves/{id}` | |
| GET/POST | `/api/admin/parents` | CRUD parents |
| GET/PUT/DELETE | `/api/admin/parents/{id}` | |
| GET/POST | `/api/admin/users` | CRUD users (caissiers, surveillants) |
| GET/PUT/DELETE | `/api/admin/users/{id}` | |
| GET/POST | `/api/admin/enseignants` | CRUD enseignants |
| GET/PUT/DELETE | `/api/admin/enseignants/{id}` | |
| GET/POST | `/api/admin/classes` | CRUD classes |
| GET/PUT/DELETE | `/api/admin/classes/{id}` | |
| GET/POST | `/api/admin/matieres` | CRUD matières |
| GET/POST | `/api/admin/matieres-classes` | Affectation matière/classe/enseignant |
| GET/POST | `/api/admin/emplois-du-temps` | CRUD emplois du temps |
| GET/POST | `/api/admin/absences-eleves` | CRUD absences élèves |
| POST | `/api/admin/absences-eleves/{id}/approuver` | Approuver absence |
| POST | `/api/admin/absences-eleves/{id}/rejeter` | Rejeter absence |
| GET/POST | `/api/admin/notes` | CRUD notes |
| GET/POST | `/api/admin/bulletins` | CRUD bulletins, download |
| GET/POST | `/api/admin/reclamations` | CRUD réclamations |
| GET/POST | `/api/admin/paiements` | CRUD paiements |
| GET/POST | `/api/admin/calendrier-scolaire` | CRUD calendrier |
| GET | `/api/admin/reports/rapport-trimestre` | Rapport trimestre |

### Vie scolaire (v1)

| Méthode | URL | Rôle | Description |
|---------|-----|------|-------------|
| GET/POST | `/api/v1/emplois-du-temps` | ADMIN, SURVEILLANT | Emplois du temps |
| GET/POST | `/api/v1/absences-eleves` | ADMIN, SURVEILLANT | Absences élèves |
| POST | `/api/v1/absences-eleves/{id}/approuver` | ADMIN, SURVEILLANT | |
| POST | `/api/v1/absences-eleves/{id}/rejeter` | ADMIN, SURVEILLANT | |
| GET/POST | `/api/v1/convocations` | ADMIN, SURVEILLANT | Convocations |
| PATCH | `/api/v1/convocations/{id}/compte-rendu` | | |

### Inscriptions et paiements (v1)

| Méthode | URL | Rôle | Description |
|---------|-----|------|-------------|
| GET/POST | `/api/v1/inscriptions` | ADMIN, CAISSIER | Inscriptions |
| GET/PATCH/DELETE | `/api/v1/inscriptions/{id}` | | |
| PATCH | `/api/v1/inscriptions/{id}/transferer` | | Transférer élève |
| GET/POST/PUT/DELETE | `/api/v1/paiements` | ADMIN, CAISSIER | Paiements |
| POST | `/api/v1/liens-paiement/generer` | ADMIN, CAISSIER | Générer lien paiement OTP |
| GET | `/api/v1/liens-paiement/{token}/detail` | Public (token) | Détail lien |
| POST | `/api/v1/liens-paiement/{token}/verifier-otp` | | Vérifier OTP |
| POST | `/api/v1/liens-paiement/{token}/payer` | | Payer |

### Liens bulletin (v1)

| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/v1/liens-bulletin/generer` | Générer lien bulletin parent |
| GET | `/api/v1/liens-bulletin/{token}/consulter` | Consulter (avec token) |
| POST | `/api/v1/liens-bulletin/{token}/verifier-otp` | Vérifier OTP pour accès |

### RH (v1)

| Méthode | URL | Description |
|---------|-----|-------------|
| GET/POST | `/api/v1/personnel` | CRUD personnel |
| GET/PATCH | `/api/v1/personnel/{id}` | |
| GET/POST | `/api/v1/pointages` | Pointages |
| GET | `/api/v1/pointages/rapport` | Rapport pointage |
| GET/POST | `/api/v1/absences-personnel` | Absences personnel |
| PATCH | `/api/v1/absences-personnel/{id}/valider` | Valider |
| PATCH | `/api/v1/absences-personnel/{id}/refuser` | Refuser |

### Espace professeur (`/api/enseignant/`)

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/enseignant/profil` | Profil |
| GET | `/api/enseignant/classes-matieres` | Classes et matières assignées |
| GET | `/api/enseignant/emploi-du-temps` | Son emploi du temps |
| GET/POST | `/api/enseignant/notes` | Notes (ses classes) |
| PUT | `/api/enseignant/notes/{noteId}` | Modifier une note |
| GET | `/api/enseignant/bulletins` | Bulletins |
| POST | `/api/enseignant/absences` | Déclarer absence |
| GET/POST | `/api/enseignant/reclamations` | Réclamations |
| POST/GET | `/api/enseignant/appels` | Appels (présence) |
| PATCH | `/api/enseignant/appels/{appelId}/soumettre` | Soumettre appel |
| POST/GET | `/api/enseignant/cahier-texte` | Cahier de texte |
| PATCH | `/api/enseignant/cahier-texte/{id}` | Modifier cahier |

### Espace élève (`/api/eleve/`)

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/eleve/profil` | Profil |
| GET | `/api/eleve/notes` | Ses notes |
| GET | `/api/eleve/bulletins` | Bulletins validés uniquement |
| GET | `/api/eleve/emploi-du-temps` | Son EDT |
| GET | `/api/eleve/absences` | Ses absences |
| GET/POST | `/api/eleve/reclamations` | Réclamations |
| GET | `/api/eleve/notifications` | Notifications |
| PATCH | `/api/eleve/notifications/{id}/lire` | Marquer comme lue |

### Accès parent par liens (pas de connexion)

Le parent ne se connecte pas. Il clique sur un lien reçu (email/SMS), éventuellement vérifie un OTP, puis consulte le contenu.

| Type | Endpoints | Description |
|------|-----------|-------------|
| **Bulletin** | `POST /api/v1/liens-bulletin/generer` (ADMIN) | Générer un lien bulletin parent |
| | `GET /api/v1/liens-bulletin/{token}/consulter` | Consulter le bulletin (public, token) |
| | `POST /api/v1/liens-bulletin/{token}/verifier-otp` | Vérifier OTP pour accès |
| **Paiement** | `POST /api/v1/liens-paiement/generer` (ADMIN/CAISSIER) | Générer un lien paiement |
| | `GET /api/v1/liens-paiement/{token}/detail` | Détail du paiement à effectuer |
| | `POST /api/v1/liens-paiement/{token}/verifier-otp` | Vérifier OTP |
| | `POST /api/v1/liens-paiement/{token}/payer` | Effectuer le paiement |
| **Absences, EDT, Convocations** | *Liens à prévoir* | Même principe : lien envoyé au parent, consultation sans connexion |

### Caisse (`/api/caisse/`)

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/caisse/paiements` | Liste paiements |
| GET | `/api/caisse/paiements/statut/{statut}` | Par statut |
| GET | `/api/caisse/paiements/historique` | Historique |
| GET | `/api/caisse/paiements/{id}` | Détail |
| POST | `/api/caisse/paiements/{id}/valider` | Valider |

### Plateforme (`/api/platform/`)

| Méthode | URL | Description |
|---------|-----|-------------|
| GET/POST | `/api/platform/tenants` | Tenants |
| GET/PUT/DELETE | `/api/platform/tenants/{id}` | CRUD tenant |
| POST | `/api/platform/tenants/{id}/suspend` | Suspendre |
| POST | `/api/platform/tenants/{id}/reactivate` | Réactiver |
| POST | `/api/platform/tenants/auto-register` | Inscription auto |
| GET/POST/DELETE | `/api/platform/utilisateurs` | Utilisateurs plateforme |
| PATCH | `/api/platform/utilisateurs/{id}/actif` | Activer/désactiver |
| GET | `/api/platform/stats` | Stats plateforme |
| GET | `/api/platform/audit-logs` | Logs d'audit |

---

## 6. Format des réponses et codes d'erreur

### Succès

- **Liste** : souvent paginée `{ "data": [...], "page", "size", "total", "totalPages" }`
- **Objet** : `{ "data": { ... } }` ou objet direct selon l'endpoint

### Erreur (toujours ce format)

```json
{
  "code": "IDENTIFIANTS_INVALIDES",
  "message": "Identifiants invalides",
  "details": [],
  "correlationId": "uuid",
  "timestamp": "2025-03-08T12:00:00Z"
}
```

| Code | HTTP | Signification |
|------|------|---------------|
| `IDENTIFIANTS_INVALIDES` | 401 | Login échoué |
| `COMPTE_VERROUILLE` | 423 | Trop de tentatives |
| `ACCES_REFUSE` | 403 | Rôle insuffisant ou hors périmètre |
| `RESSOURCE_INTROUVABLE` | 404 | Entité non trouvée |
| `EMAIL_DEJA_UTILISE` | 409 | Doublon email |
| `VALIDATION_ECHOUEE` | 400 | Données invalides |
| `REGLE_METIER_VIOLEE` | 422 | Règle métier non respectée |
| `TENANT_INACTIF` | 403 | Établissement suspendu |
| `OTP_INVALIDE` | 401 | Code OTP incorrect |
| `MOT_DE_PASSE_TROP_COURT` | 400 | Moins de 12 caractères |
| `TOO_MANY_REQUESTS` | 429 | Rate limit dépassé |

---

## 7. Conventions et normes

### IDs

- Tous les IDs sont des **UUID** (ex. `550e8400-e29b-41d4-a716-446655440000`)
- Jamais d'auto-incrément exposé en API

### Dates

- Format **ISO 8601** : `2025-03-08T12:00:00Z`
- Stockage en UTC

### Pagination

- Paramètres : `?page=0&size=20`
- Tri : `?sort=nom,asc` ou `?sort=createdAt,desc`

### Headers recommandés

- `Content-Type: application/json`
- `Accept: application/json`
- `Authorization: Bearer <token>`
- `X-Tenant-Id: <uuid>` (pour les requêtes métier d'un tenant)

---

## 8. Ce qui reste à faire / à fournir

### Côté backend (à livrer)

| Élément | Statut | Détail |
|---------|--------|--------|
| Spécification OpenAPI exportable | ✅ | `/q/openapi` en dev |
| Collection Postman | ✅ | `postman/Noura-School-API.postman_collection.json` |
| Variables d'environnement (prod) | À fournir | Voir `docs/DEPLOIEMENT.md` |
| URL de base API (prod/staging) | À définir | Pour le frontend |
| CORS configuré pour le frontend | À vérifier | Origines autorisées |

### Fonctionnalités à finaliser

| Fonctionnalité | Priorité | Description |
|----------------|----------|-------------|
| Intégration WhatsApp/Twilio | Basse | Templates réels (stubs en place) |
| Parent : connexion OTP | Moyenne | Accès parent par lien + OTP (liens bulletin/paiement existent) |
| Tests charge 100 tenants | Basse | Simulation Gatling étendue |
| Scan OWASP ZAP (staging) | Moyenne | Revues sécurité |

### Ce que le frontend doit fournir / savoir

1. **URL de base** : à configurer (ex. `VITE_API_URL` ou `NEXT_PUBLIC_API_URL`)
2. **Header X-Tenant-Id** : récupérer `tenantId` depuis `/me` et l'envoyer sur toutes les requêtes métier
3. **Gestion du refresh token** : avant expiration du JWT (15 min), appeler `/refresh` pour obtenir un nouveau token
4. **Gestion 401** : si `ACCES_REFUSE` ou token expiré → rediriger vers login
5. **Codes d'erreur** : utiliser le champ `code` pour afficher des messages utilisateur cohérents
6. **Pagination** : respecter `page`, `size`, `total`, `totalPages` pour les listes
7. **Mot de passe** : minimum 12 caractères pour création et réinitialisation

### Fichiers utiles

| Fichier | Usage |
|---------|-------|
| `postman/Noura-School-API.postman_collection.json` | Tests manuels, exemples de requêtes |
| `docs/DEPLOIEMENT.md` | Variables d'environnement, déploiement |
| `docs/SPEC_AUTH_ET_ROLES.md` | Spécification auth et rôles |
| `docs/SUIVI_PROJET.md` | État d'avancement |
| `task.md` | Référence métier complète |

---

*Noura School — Documentation globale v1.0 · Dernière mise à jour : mars 2025*
