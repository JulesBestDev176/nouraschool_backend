# NOURA SCHOOL — Liste des tâches (validation par étape)

> Référence : **task.md** · Coche `[x]` quand une étape est validée.
> Priorités : 🔴 Urgent · 🟠 High · 🟡 Normal · 🟢 Low

---

## 📁 FOLDER 1 — SETUP & INFRASTRUCTURE

### 📋 List : Initialisation projet

- [x] 🔴 Créer le projet Quarkus 3.x avec Java 21
  - [x] Configurer `pom.xml` (extensions : resteasy-reactive, hibernate-orm-panache, jdbc-postgresql, security-jwt, flyway, smallrye-openapi)
  - [x] Configurer les profils `dev`, `test`, `prod`
  - [x] Configurer `application.yml` de base
  - [x] Ajouter `.gitignore`, `.env.example`

- [x] 🔴 Mettre en place la structure des packages
  - [x] `com.nouraschool.runtime` (resource, config, security, aop, runner)
  - [x] `com.nouraschool.domain` (service, repository, entity, dto, mapper, usecases, exception)
  - [x] *Convention : package racine `com.nouraschool` (et non `sn.nouraschool.api`)*

- [x] 🔴 Configurer PostgreSQL
  - [x] Base de données locale / DevServices
  - [x] Configurer Flyway
  - [x] Migrations initiales (V1–V8)

- [x] 🔴 Configurer Redis
  - [x] Connexion Redis locale
  - [x] Service utilitaire `RedisService` (get/set/delete/expire)

- [x] 🔴 Configurer Docker
  - [x] `Dockerfile` multi-stage (build JDK 21 → runtime JRE 21)
  - [x] `docker-compose.yml` (app + postgres + redis)
  - [x] Utilisateur non-root dans le Dockerfile

- [x] 🟠 Configurer les outils de qualité
  - [x] Checkstyle (Google Java Style)
  - [x] SpotBugs
  - [x] PMD
  - [x] Jacoco (seuil minimum 80%)

- [x] 🟠 Configurer CI/CD (GitHub Actions ou GitLab CI)
  - [x] Pipeline : lint → test → coverage → build → scan image
  - [x] Branch protection sur `main` et `develop`

---

## 📁 FOLDER 2 — FONDATIONS TECHNIQUES

### 📋 List : Composants transversaux

- [x] 🔴 Gestion multi-tenant
  - [x] Table `tenant` + colonne `tenant_id` sur utilisateurs
  - [x] Créer `TenantContext` (`@RequestScoped`)
  - [x] Créer `TenantResolver` (résolution depuis header `X-Tenant-Id`)
  - [x] Configurer filtre Hibernate `@TenantFilter` sur toutes les entités
  - [x] Tester l'isolation cross-tenant

- [x] 🔴 Gestion globale des exceptions
  - [x] `DefaultExceptionHandler` (`@ServerExceptionMapper`)
  - [x] Handler `ConstraintViolationException` → 400
  - [x] Handler `NotFoundException` → 404
  - [x] Handler `InvalidRequestException` / `ServiceException` → 4xx/5xx
  - [x] Handler générique → 500 (sans stack trace)
  - [x] `ErrorDto` (code, message, details, correlationId, timestamp)
  - [x] Codes d’erreur spécifiques (application.yml / ApplicationProperties)

- [x] 🔴 Correlation ID
  - [x] correlationId dans chaque réponse d’erreur (ErrorDto)
  - [x] Créer `CorrelationFilter` (injecte UUID dans MDC + header réponse)
  - [x] Format log : `[NomClasse][nomMethode] message {}`

- [x] 🔴 Pagination standard
  - [x] `PageDTO<T>` / `PageRequest` existants
  - [x] Créer `PageUtils` helper si besoin

- [x] 🟠 Audit log
  - [x] Table `audit_log` (migration V8)
  - [x] Envoi vers Elasticsearch si activé (`AuditLogService`, `ElasticAuditLogServiceImpl`)
  - [x] Créer `AuditService` en base PostgreSQL (persist + Elastic)
  - [x] Injecter dans les services métier (LOGIN_SUCCESS, LOGOUT déjà branchés)

- [x] 🟠 Notification log
  - [x] Créer entité `NotificationLog`
  - [x] Migration Flyway
  - [x] Créer `NotificationLogService`

- [x] 🟠 Upload de fichiers (S3)
  - [x] Configurer client S3 (Quarkus S3 extension)
  - [x] Créer `StorageService` (upload, getPresignedUrl, delete)
  - [x] URLs pré-signées avec expiration 15 min

- [x] 🟡 Headers de sécurité HTTP
  - [x] Configurer HSTS, X-Frame-Options, CSP, etc. dans `application.yml`

- [x] 🟡 Configurer OpenAPI / Swagger UI
  - [x] SwaggerConfig existant
  - [x] Accessible en dev uniquement : `/q/swagger-ui`

---

## 📁 FOLDER 3 — MODULE AUTH & RÔLES

### 📋 List : Base de données Auth

- [x] Migration `tenant` + `plateforme_utilisateur`
- [x] Table `users` avec `tenant_id`, `must_change_password`
- [x] Table `refresh_tokens`
- [x] Table `surveillants` + `surveillant_cycle` (à compléter si besoin)
- [x] Table `audit_log`

### 📋 List : Entités & Repositories Auth

- [x] Entité `TenantEntity` + `TenantRepository`
- [x] Entité `UserEntity` (et sous-types : Admin, Caissier, Surveillant, Enseignant, Eleve, Parent) + `UserRepository`
  - [x] Méthode `findByUsernameOrEmailOrPhone` (login email ou téléphone)
- [x] Entité `RefreshToken` + `RefreshTokenRepository`
  - [x] Méthode `findByTokenHash`, `revokeByUserId`

### 📋 List : Services Auth

- [x] JWT (RS256, 15 min, refresh 7 j, stocké hashé)
- [x] `AuthService`
  - [x] `login(login, password)` → email ou téléphone + bcrypt + tenant actif + compte actif
  - [x] Verrouillage après 5 échecs (Redis counter, TTL 10 min)
  - [x] Délai exponentiel entre tentatives
  - [x] `refresh(refreshToken)` → nouveau token
  - [x] Rotation stricte + détection de vol
  - [x] `logout(userId)` → révocation
  - [x] `forgotPassword(email)` → token Redis 1h + envoi email
  - [x] `resetPassword(token, newPassword)`
  - [x] `changePassword(userId, oldPassword, newPassword)`
  - [x] Enregistrement audit (Elastic si activé) LOGIN_SUCCESS / LOGOUT
  - [x] `/me` : cycles pour SURVEILLANT (codes des cycles assignés)

- [x] `PasswordEncoder` (bcrypt, cost configurable via `app.bcrypt.cost`)

### 📋 List : Resources Auth

- [x] `AuthResource` `/api/v1/auth`
  - [x] `POST /login`
  - [x] `POST /refresh`
  - [x] `POST /logout`
  - [x] `GET /me`
  - [x] `POST /forgot-password`
  - [x] `POST /reset-password`
  - [x] `POST /change-password`

- [x] 🔴 Rate limiting Auth (Redis)
  - [x] 5 tentatives login / IP / 10 min
  - [x] 3 demandes forgot-password / email / 15 min

### 📋 List : Tests Auth

- [x] Tests unitaires `AuthService`
- [x] Tests unitaires `JwtService`
- [x] Tests intégration `AuthResource`

---

## 📁 FOLDER 4 — MODULE PLATEFORME (SUPER ADMIN)

### 📋 List : Services & Resources Plateforme

- [x] 🟠 `TenantService` (create, findAll, findById, update, suspend, reactivate, delete, autoRegister)
- [x] 🟠 `PlateformeStatsService`
- [x] 🟠 `PlateformeUtilisateurService`
- [x] 🟠 `TenantResource` (`/api/platform/tenants`)
- [x] 🟠 `PlateformeUtilisateurResource` (`/api/platform/utilisateurs`)
- [x] 🟠 `PlateformeStatsResource` (`/api/platform/stats`)
- [x] 🟠 `AuditLogResource` (`/api/platform/audit-logs`)

### 📋 List : Tests Plateforme

- [x] Tests création tenant → admin initial créé
- [x] Tests suspension → tous les users bloqués
- [x] Tests accès GESTIONNAIRE vs SUPER_ADMIN

---

## 📁 FOLDER 5 — MODULE GESTION ÉTABLISSEMENT (ADMIN)

### 📋 List : Base de données Établissement

- [x] 🟠 Migrations : `cycle`, `niveau`, `batiment`, `salle`, `annee_academique`, `cours`
- [x] `tenant_id` sur `classes`, `matieres`, `matiere_classe`, `emplois_du_temps`
- [x] `niveau_id`, `annee_academique_id`, `salle_id` sur `classes` (nullable, migration progressive)
- [x] *Partiellement : classes, matières, emplois du temps existants (schéma actuel)*

### 📋 List : Services & Resources Établissement

- [x] 🟠 CycleService, NiveauService, BatimentService, SalleService
- [x] 🟠 AnneeAcademiqueService (PeriodeAcademiqueService à venir)
- [x] MatiereService, ClasseService (AdminMatiereUseCase, AdminClasseUseCase existants)
- [x] CoursService (avec **coefficient**)
- [x] Resources : `/api/v1/cycles`, `/api/v1/niveaux`, `/api/v1/batiments`, `/api/v1/salles`, `/api/v1/annees-academiques`, `/api/v1/cours`
- [x] AnnonceService, EtablissementStatsService

### 📋 List : Gestion des utilisateurs (Admin)

- [x] Création utilisateurs (Admin : caissiers, surveillants, enseignants, élèves, parents ; Caissier : élèves, parents)
- [x] 🟠 `UtilisateurService` complet (reinitialiserMdp, assignerCycles, retirerCycle)
- [x] 🟠 `UtilisateurResource` `/api/v1/utilisateurs` (POST, GET, PATCH, DELETE, reinitialiser-mdp, cycles)
- [x] Politique mot de passe 12 caractères min (reset, change, reinitialiserMdp)

### 📋 List : Tests Établissement

- [x] Tests CRUD cycles, niveaux, matières
- [x] Test activation année académique
- [x] Test protection dernier admin (delete + désactivation via PATCH)
- [x] Test isolation tenant

---

## 📁 FOLDER 6 — MODULE RH & POINTAGE

### 📋 List : Base de données RH

- [x] 🟡 Migration `personnel`, `pointage`, `absence_personnel`

### 📋 List : Services & Resources RH

- [x] 🟡 PersonnelService, PointageService, AbsencePersonnelService
- [x] 🟡 PersonnelResource, PointageResource, AbsencePersonnelResource

### 📋 List : Tests RH

- [x] 🟡 Tests pointage, validation absence, permissions RH vs ADMIN

---

## 📁 FOLDER 7 — MODULE INSCRIPTIONS & PAIEMENTS

### 📋 List : Base de données Inscriptions

- [x] Table `parent`, `eleve_parent` (schéma actuel)
- [x] 🟠 Migrations : `inscription`, `lien_paiement_parent`, `tenant_id` et `inscription_id` sur paiements
- [ ] *Paiement : API Payetech prévue (config `app.payment.provider=payetech`)*

### 📋 List : Services & Resources Inscriptions

- [x] ParentService / ParentRepository (CRUD)
- [x] EleveService / AdminEleveResource (création par Admin/Caissier)
- [x] 🟠 InscriptionService (transaction élève + parent + inscription)
- [x] 🟠 PaiementService (AdminPaiementUseCase + PaiementResource)
- [x] 🟠 LienPaiementService (OTP)
- [x] 🟠 Resources : `/api/v1/inscriptions`, `/api/v1/paiements`, `/api/v1/liens-paiement`

### 📋 List : Tests Inscriptions

- [x] Tests inscription complète, effectif max, doublon inscription, OTP, lien paiement

---

## 📁 FOLDER 8 — MODULE VIE SCOLAIRE (SURVEILLANT)

### 📋 List : Base de données Vie scolaire

- [x] Tables notes, bulletins, absences (schéma actuel)
- [x] 🟡 Migrations : convocation, lien_bulletin_parent, publie/statut sur emplois_du_temps/absences/bulletins

### 📋 List : Services & Resources Vie scolaire

- [x] 🟡 EmploiDuTempsService (AdminEmploiDuTempsUseCase), AbsenceEleveService (AdminAbsenceEleveUseCase), NoteService, BulletinService
- [x] 🟡 ConvocationService, LienBulletinService
- [x] 🟡 Resources : emplois-du-temps, absences-eleves, notes, bulletins, convocations, liens-bulletin

### 📋 List : Tests Vie scolaire

- [x] 🟡 Approuver/rejeter absence, accès surveillant, ENSEIGNANT 403 sur EDT

---

## 📁 FOLDER 9 — MODULE ESPACE PROFESSEUR

### 📋 List : Base de données Professeur

- [x] 🟡 Migration `appel`, `appel_ligne`, `cahier_texte`

### 📋 List : Services & Resources Professeur

- [x] 🟡 AppelService, NoteProfesseurService, CahierTexteService
- [x] 🟡 AppelResource, CahierTexteResource, MonEmploiDuTempsResource

### 📋 List : Tests Professeur

- [x] 🟡 Appel → absences auto, saisie note période verrouillée, périmètre professeur

---

## 📁 FOLDER 10 — MODULE ESPACE ÉLÈVE

### 📋 List : Base de données Élève

- [x] 🟡 Migration `notification`, `reclamation_note`

### 📋 List : Services & Resources Élève

- [x] 🟡 EspaceEleveService, ReclamationNoteService, NotificationService
- [x] 🟡 Resources : mon-profil, mes-notes, mes-bulletins, mes-absences, reclamations-notes

### 📋 List : Tests Élève

- [x] 🟡 Élève ne voit que ses données, notification absence, bulletin non validé

---

## 📁 FOLDER 11 — MODULE NOTIFICATIONS

### 📋 List : Intégrations

- [x] 🟢 EmailService (SMTP, templates : compte, reset mdp, bulletin)
- [x] 🟢 WhatsAppService (templates : absence, convocation, bulletin, rappel paiement)
- [x] 🟢 SmsService (Twilio, OTP fallback)
- [x] 🟢 OtpService (Redis, 6 chiffres, TTL 5 min, max 3 tentatives)

### 📋 List : Tests Notifications

- [x] 🟢 Mock SMTP, OTP, asynchrone + notification_log

---

## 📁 FOLDER 12 — TESTS & QUALITÉ

### 📋 List : Tests transversaux

- [x] 🟠 @QuarkusTest + TestContainers (PostgreSQL + Redis)
- [x] 🟠 Fixtures de données de test
- [x] 🟠 Tests sécurité cross-tenant, RBAC par rôle
- [x] 🟡 Tests de charge (Gatling)
- [x] 🟡 100 tenants simultanés

### 📋 List : Documentation

- [x] 🟡 Tous les endpoints annotés OpenAPI
- [x] 🟡 Générer et valider la spec OpenAPI
- [x] 🟢 Guide de déploiement

---

## 📁 FOLDER 13 — DÉPLOIEMENT & PRODUCTION

### 📋 List : Préparation prod

- [x] 🟠 `application-prod.yml` (secrets via env, Hibernate validate, logs JSON, Swagger désactivé)
- [x] 🟠 Secrets (Vault ou CI/CD) : DB, Redis, JWT, S3, WhatsApp, Twilio, SMTP
- [x] 🟡 Health checks : `/q/health/live`, `/q/health/ready`
- [x] 🟡 Alertes monitoring (5xx, latence)
- [x] 🟢 Staging + revue sécurité (OWASP)

---

## 📌 Tags suggérés

```
backend · auth · sécurité · base-de-données · api · test · notification ·
super-admin · admin · caissier · surveillant · professeur · eleve · parent · rh
```

## 📊 Estimation globale

| Folder | Tâches | Estimation |
|--------|--------|------------|
| 1 — Setup & Infrastructure | 7 | 3 jours |
| 2 — Fondations techniques | 8 | 4 jours |
| 3 — Auth & Rôles | 20+ | 5 jours |
| 4 — Plateforme Super Admin | 12 | 4 jours |
| 5 — Gestion Établissement | 20 | 6 jours |
| 6 — RH & Pointage | 8 | 3 jours |
| 7 — Inscriptions & Paiements | 18 | 6 jours |
| 8 — Vie scolaire | 16 | 6 jours |
| 9 — Espace Professeur | 10 | 4 jours |
| 10 — Espace Élève | 12 | 4 jours |
| 11 — Notifications | 8 | 3 jours |
| 12 — Tests & Qualité | 6 | 3 jours |
| 13 — Déploiement | 8 | 2 jours |
| **TOTAL** | **~150** | **~53 jours** |

---

*Ordre recommandé : Folders 1 → 2 → 3 → 4 → 5 → 7 → 8 → 9 → 10 → 6 → 11 → 12 → 13*

*Noura School — Liste des tâches v1.0 · Cochez `[x]` au fur et à mesure.*
