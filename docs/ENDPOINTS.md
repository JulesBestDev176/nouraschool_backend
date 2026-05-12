# Endpoints — Noura School Backend

Point important : les routes `/api/platform/*` sont pour la **plateforme globale**. Les routes `/api/admin/*`, `/api/v1/*`, `/api/enseignant/*`, `/api/eleve/*`, `/api/parent/*`, `/api/caisse/*` sont dans le contexte d'une **école / tenant**.

---

## Public

Ces endpoints ne nécessitent pas de token :

| Méthode | Endpoint | Usage |
|---------|----------|-------|
| `POST` | `/api/v1/auth/login` | Connexion école ou plateforme |
| `POST` | `/api/v1/auth/refresh` | Refresh token |
| `POST` | `/api/v1/auth/forgot-password` | Mot de passe oublié |
| `POST` | `/api/v1/auth/reset-password` | Réinitialiser mot de passe |
| `GET` | `/api/v1/liens-bulletin/{token}/consulter` | Consultation bulletin via lien |
| `POST` | `/api/v1/liens-bulletin/{token}/verifier-otp` | Vérification OTP bulletin |
| `GET` | `/api/v1/liens-paiement/{token}/detail` | Détail lien paiement |
| `POST` | `/api/v1/liens-paiement/{token}/verifier-otp` | Vérification OTP paiement |
| `POST` | `/api/v1/liens-paiement/{token}/payer` | Paiement via lien |

---

## Plateforme Globale

Rôles : `SUPER_ADMIN`, `GESTIONNAIRE`.

| Méthode | Endpoint | Rôles |
|---------|----------|-------|
| `GET` | `/api/platform/tenants` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `POST` | `/api/platform/tenants` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `GET` | `/api/platform/tenants/{id}` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `PUT` | `/api/platform/tenants/{id}` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `POST` | `/api/platform/tenants/auto-register` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `POST` | `/api/platform/tenants/{id}/suspend` | `SUPER_ADMIN` |
| `POST` | `/api/platform/tenants/{id}/reactivate` | `SUPER_ADMIN` |
| `DELETE` | `/api/platform/tenants/{id}` | `SUPER_ADMIN` |
| `GET` | `/api/platform/utilisateurs` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `POST` | `/api/platform/utilisateurs` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `GET` | `/api/platform/utilisateurs/{id}` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `DELETE` | `/api/platform/utilisateurs/{id}` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `PATCH` | `/api/platform/utilisateurs/{id}/actif` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `GET` | `/api/platform/stats` | `SUPER_ADMIN`, `GESTIONNAIRE` |
| `GET` | `/api/platform/audit-logs` | `SUPER_ADMIN`, `GESTIONNAIRE` |

---

## ADMIN École

L'`ADMIN` agit uniquement dans son tenant avec `X-Tenant-Id`.

### CRUD principal

| Ressource | Endpoints |
|-----------|-----------|
| Utilisateurs école | `/api/v1/utilisateurs`, `/api/admin/users` |
| Années académiques | `/api/v1/annees-academiques` |
| Cycles | `/api/v1/cycles` |
| Niveaux | `/api/v1/niveaux` |
| Bâtiments | `/api/v1/batiments` |
| Salles | `/api/v1/salles` |
| Classes | `/api/v1/classes`, `/api/admin/classes` |
| Enseignants | `/api/v1/enseignants`, `/api/admin/enseignants` |
| Élèves | `/api/v1/eleves`, `/api/admin/eleves` |
| Parents | `/api/v1/parents`, `/api/admin/parents` |
| Matières | `/api/admin/matieres` |
| Matières/classes | `/api/admin/matieres-classes` |
| Cours | `/api/v1/cours` |
| Calendrier scolaire | `/api/admin/calendrier-scolaire` |
| Stats établissement | `/api/v1/stats/etablissement` |
| Reports | `/api/admin/reports/rapport-trimestre` |
| Réclamations admin | `/api/admin/reclamations` |

Méthodes habituelles sur ces ressources : `GET`, `POST`, `GET /{id}`, `PUT /{id}` ou `PATCH /{id}`, `DELETE /{id}` selon le controller.

---

## ADMIN + CAISSIER

| Ressource | Endpoints |
|-----------|-----------|
| Paiements | `/api/v1/paiements`, `/api/admin/paiements` |
| Inscriptions | `/api/v1/inscriptions` |
| Élèves | `/api/v1/eleves`, `/api/admin/eleves` |
| Parents | `/api/v1/parents`, `/api/admin/parents` |
| Liens paiement | `POST /api/v1/liens-paiement/generer` |

Extras :
- `PATCH /api/v1/inscriptions/{id}/transferer`

---

## ADMIN + SURVEILLANT

| Ressource | Endpoints |
|-----------|-----------|
| Absences élèves | `/api/v1/absences-eleves`, `/api/admin/absences-eleves` |
| Notes | `/api/admin/notes` |
| Bulletins | `/api/admin/bulletins` |
| Emplois du temps | `/api/v1/emplois-du-temps`, `/api/admin/emplois-du-temps` |
| Convocations | `/api/v1/convocations` |
| Liens bulletin | `POST /api/v1/liens-bulletin/generer` |

Extras :
- `POST /api/v1/absences-eleves/{id}/approuver`
- `POST /api/v1/absences-eleves/{id}/rejeter`
- `GET /api/admin/bulletins/{id}/download`
- `GET /api/admin/bulletins/download/by-classe`
- `GET /api/admin/bulletins/download/all`
- `PATCH /api/v1/convocations/{id}/compte-rendu`

---

## ADMIN + RH

| Ressource | Endpoints |
|-----------|-----------|
| Personnel | `/api/v1/personnel` |
| Pointages | `/api/v1/pointages` |
| Absences personnel | `/api/v1/absences-personnel` |

Extras :
- `GET /api/v1/pointages/rapport`
- `PATCH /api/v1/absences-personnel/{id}/valider`
- `PATCH /api/v1/absences-personnel/{id}/refuser`

---

## CAISSIER Seul

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/caisse/paiements` |
| `GET` | `/api/caisse/paiements/{id}` |
| `POST` | `/api/caisse/paiements/{id}/valider` |
| `GET` | `/api/caisse/paiements/historique` |
| `GET` | `/api/caisse/paiements/statut/{statut}` |

---

## ENSEIGNANT

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/enseignant/profil` |
| `GET` | `/api/enseignant/classes-matieres` |
| `GET` | `/api/enseignant/emploi-du-temps` |
| `GET` | `/api/enseignant/notes` |
| `POST` | `/api/enseignant/notes` |
| `PUT` | `/api/enseignant/notes/{noteId}` |
| `POST` | `/api/enseignant/absences` |
| `GET` | `/api/enseignant/bulletins` |
| `GET` | `/api/enseignant/reclamations` |
| `POST` | `/api/enseignant/appels` |
| `GET` | `/api/enseignant/appels` |
| `PATCH` | `/api/enseignant/appels/{appelId}/soumettre` |
| `POST` | `/api/enseignant/cahier-texte` |
| `GET` | `/api/enseignant/cahier-texte` |
| `PATCH` | `/api/enseignant/cahier-texte/{id}` |

---

## ÉLÈVE

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/eleve/profil` |
| `GET` | `/api/eleve/notes` |
| `GET` | `/api/eleve/bulletins` |
| `GET` | `/api/eleve/emploi-du-temps` |
| `GET` | `/api/eleve/absences` |
| `GET` | `/api/eleve/notifications` |
| `PATCH` | `/api/eleve/notifications/{id}/lire` |
| `GET` | `/api/eleve/reclamations` |
| `POST` | `/api/eleve/reclamations` |

---

## PARENT

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/parent/profil` |
| `GET` | `/api/parent/enfants` |
| `GET` | `/api/parent/enfants/{eleveId}/profil` |
| `GET` | `/api/parent/enfants/{eleveId}/notes` |
| `GET` | `/api/parent/enfants/{eleveId}/bulletins` |
| `GET` | `/api/parent/enfants/{eleveId}/emploi-du-temps` |
| `GET` | `/api/parent/enfants/{eleveId}/absences` |
| `GET` | `/api/parent/paiements` |
| `GET` | `/api/parent/notifications` |

---

## Auth Connecté

Accepte les rôles école : `ADMIN`, `CAISSIER`, `SURVEILLANT`, `ENSEIGNANT`, `ELEVE`, `PARENT`.

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/v1/auth/me` |
| `POST` | `/api/v1/auth/logout` |
| `POST` | `/api/v1/auth/change-password` |

> **Note** : les comptes plateforme `SUPER_ADMIN` / `GESTIONNAIRE` peuvent se connecter et appeler `/api/platform/*`, mais `/api/v1/auth/me` n'est pas encore adapté à leur profil. Il faudra ajouter un `/api/platform/auth/me` ou élargir `/api/v1/auth/me` pour lire aussi `plateforme_utilisateur`.
