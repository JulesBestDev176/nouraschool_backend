# Guide de déploiement — Noura School Backend

## Prérequis

- Java 21
- PostgreSQL 16
- Redis 7+
- SMTP si les emails doivent réellement partir
- S3-compatible storage ou MinIO uniquement si les fonctionnalités de stockage de fichiers sont activées

## Variables d'environnement (production)

### Base de données
- `DB_USERNAME` — utilisateur PostgreSQL
- `DB_PASSWORD` — mot de passe
- `DB_HOST` — hôte
- `DB_PORT` — port (5432)
- `DB_NAME` — nom de la base

### Redis
- `REDIS_HOST` — hôte Redis
- `REDIS_PORT` — port (6379)
- `REDIS_PASSWORD` — mot de passe (optionnel)

### JWT
- `JWT_ISSUER` — émetteur
- `JWT_AUDIENCE` — audience
- `JWT_ACCESS_TOKEN_LIFESPAN` — durée token (secondes)

### S3 (optionnel actuellement)
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `S3_BUCKET`

### Elasticsearch (optionnel)
- `ELASTIC_LOGGING_ENABLED` — activer logs Elasticsearch
- `ELASTIC_HOST`, `ELASTIC_PORT`, `ELASTIC_INDEX`

### CORS
- `CORS_ORIGINS` — origines autorisées pour le frontend

En local, le frontend `noura-school-frontend` se lance avec `npm start` / `ng serve`, donc Angular utilise par défaut :

```env
CORS_ORIGINS=http://localhost:4200,http://127.0.0.1:4200
```

En staging / Coolify, remplacer par l'URL publique du frontend :

```env
CORS_ORIGINS=https://votre-frontend-staging.example.com
```

### SMTP (optionnel mais nécessaire pour l'envoi réel d'emails)
- `QUARKUS_MAILER_HOST`
- `QUARKUS_MAILER_PORT`
- `QUARKUS_MAILER_USERNAME`
- `QUARKUS_MAILER_PASSWORD`
- `QUARKUS_MAILER_FROM`

## Profil prod

```bash
java -jar noura-school-backend-1.0.0-SNAPSHOT-runner.jar -Dquarkus.profile=prod
```

## Health checks

- **Liveness** : `GET /q/health/live`
- **Readiness** : `GET /q/health/ready`

## Docker

```bash
docker build -t noura-school-backend .
docker run -e DB_HOST=... -e REDIS_HOST=... noura-school-backend
```

## Déploiement Coolify — branche `develop`

Cette section décrit un déploiement **staging** de la branche `develop`.

Objectif :

1. Coolify héberge PostgreSQL, Redis et le backend.
2. Coolify build l'application avec `docker/Dockerfile`.
3. GitHub Actions lance la CI à chaque PR / merge.
4. Après un merge dans `develop`, GitHub Actions déclenche Coolify seulement si la CI passe.

### 0. Ce qui est réellement nécessaire

Après analyse du code :

| Service | Statut | Pourquoi |
|---------|--------|----------|
| PostgreSQL | Obligatoire | Base principale, entités métier, refresh tokens, audit logs, migrations Flyway |
| Redis | Obligatoire | Rate limiting login, verrouillage après échecs, reset-password, OTP |
| Elasticsearch | Optionnel | Les audits sont toujours stockés en PostgreSQL; Elastic est appelé uniquement si `ELASTIC_LOGGING_ENABLED=true` |
| S3 / MinIO | Optionnel actuellement | Le service `StorageServiceS3Impl` existe, mais aucun use case ne l'appelle encore dans le code actuel |
| SMTP | Optionnel au démarrage, nécessaire fonctionnellement | `EmailService` est appelé lors de la création d'un élève pour envoyer les identifiants |

Conclusion pour un premier staging `develop` : créer **PostgreSQL + Redis + backend**. Ne pas créer Elasticsearch. Ne configurer S3/MinIO et SMTP que si tu veux tester les fonctionnalités liées.

### 1. Préparer le serveur Coolify

Avant de créer l'application :

1. Vérifier que Coolify est installé et accessible.
2. Vérifier que le serveur a assez de ressources :
   - minimum conseillé : 2 vCPU, 4 Go RAM;
   - plus confortable : 2 à 4 vCPU, 8 Go RAM, surtout pendant le build Maven.
3. Préparer un domaine ou sous-domaine pour l'API, par exemple :

```text
api-dev.noura-school.com
```

4. Dans le DNS du domaine, créer un enregistrement `A` vers l'IP du serveur Coolify :

```text
api-dev.noura-school.com -> <IP_DU_SERVEUR>
```

5. Attendre la propagation DNS, puis vérifier :

```bash
nslookup api-dev.noura-school.com
```

### 2. Créer le projet et l'environnement

Dans Coolify :

1. Aller dans **Projects**.
2. Créer un projet, par exemple `noura-school`.
3. Créer ou utiliser un environnement, par exemple `develop` ou `staging`.
4. Toutes les ressources ci-dessous doivent être créées dans ce même environnement pour que les noms internes soient simples à relier.

### 3. Créer PostgreSQL

Dans le projet Coolify :

1. Cliquer sur **New Resource**.
2. Choisir **Database** puis **PostgreSQL**.
3. Utiliser PostgreSQL `16`.
4. Nommer la ressource, par exemple :

```text
noura-school-postgres-develop
```

5. Définir ou noter :

```text
Database name: noura_school_db
Username: postgres ou noura_school
Password: généré par Coolify ou mot de passe fort
Port interne: 5432
```

6. Démarrer la base.
7. Ouvrir les détails de la ressource et noter le hostname interne fourni par Coolify. Selon la configuration, il ressemble souvent au nom de service Coolify.

Ces valeurs seront utilisées côté backend :

```env
DB_HOST=<hostname-interne-postgres>
DB_PORT=5432
DB_NAME=noura_school_db
DB_USERNAME=<user-postgres>
DB_PASSWORD=<password-postgres>
```

Important : ne pas exposer PostgreSQL publiquement sauf besoin temporaire très contrôlé.

### 4. Créer Redis

Dans le projet Coolify :

1. Cliquer sur **New Resource**.
2. Choisir **Database** ou **Service**, puis **Redis**.
3. Utiliser Redis `7`.
4. Nommer la ressource :

```text
noura-school-redis-develop
```

5. Démarrer Redis.
6. Noter le hostname interne, le port et le mot de passe si Coolify en configure un.

Ces valeurs seront utilisées côté backend :

```env
REDIS_HOST=<hostname-interne-redis>
REDIS_PORT=6379
REDIS_PASSWORD=<password-redis-si-present>
```

Si Redis n'a pas de mot de passe, laisser `REDIS_PASSWORD` vide ou ne pas définir la variable.

### 5. Préparer le stockage S3 ou MinIO (optionnel)

Le backend contient une implémentation S3 (`StorageServiceS3Impl`), mais dans le code actuel aucun use case ne l'appelle directement. Tu peux donc déployer sans S3 pour le premier staging.

Configure S3 ou MinIO seulement si tu ajoutes / actives des endpoints d'upload, de documents ou de bulletins stockés dans un bucket.

Option AWS S3 :

1. Créer un bucket, par exemple `noura-school-develop-files`.
2. Créer une clé IAM avec accès limité à ce bucket.
3. Noter :

```env
AWS_REGION=us-east-1
AWS_CREDENTIALS_TYPE=static
AWS_ACCESS_KEY_ID=<access-key>
AWS_SECRET_ACCESS_KEY=<secret-key>
S3_BUCKET=noura-school-develop-files
S3_ENDPOINT_OVERRIDE=
```

Option MinIO ou stockage S3-compatible :

1. Créer le bucket dans MinIO.
2. Créer une access key et une secret key.
3. Renseigner aussi l'endpoint :

```env
AWS_REGION=us-east-1
AWS_CREDENTIALS_TYPE=static
AWS_ACCESS_KEY_ID=<access-key>
AWS_SECRET_ACCESS_KEY=<secret-key>
S3_BUCKET=noura-school-develop-files
S3_ENDPOINT_OVERRIDE=https://minio.example.com
```

### 6. Créer l'application backend

Dans Coolify :

1. Cliquer sur **New Resource**.
2. Choisir **Application**.
3. Sélectionner le dépôt Git du backend.
4. Sélectionner la branche :

```text
develop
```

5. Choisir le mode de build Dockerfile.
6. Configurer :

```text
Build Pack: Dockerfile
Base directory / Build context: .
Dockerfile location: docker/Dockerfile
Port: 8080
```

7. Ajouter le domaine :

```text
https://api-dev.noura-school.com
```

8. Activer HTTPS / Let's Encrypt si Coolify le propose.
9. Configurer le health check :

```text
Path: /q/health/ready
Port: 8080
```

Le conteneur lance le jar Quarkus avec :

```text
java -jar /app/quarkus-run.jar
```

En mode jar, Quarkus utilise le profil runtime `prod` par défaut. Les migrations Flyway s'exécutent au démarrage via `quarkus.flyway.migrate-at-start=true`.

### 7. Ajouter les variables d'environnement dans Coolify

Dans l'application backend Coolify, ouvrir **Environment Variables** et ajouter :

```env
QUARKUS_DEV_SERVICES_ENABLED=false
CORS_ORIGINS=https://votre-frontend-staging.example.com

DB_HOST=<hostname-interne-postgres>
DB_PORT=5432
DB_NAME=noura_school_db
DB_USERNAME=<user-postgres>
DB_PASSWORD=<password-postgres>

REDIS_HOST=<hostname-interne-redis>
REDIS_PORT=6379
REDIS_PASSWORD=<password-redis-si-configure>

JWT_ISSUER=https://api-dev.noura-school.com
JWT_AUDIENCE=noura-school-api
JWT_ACCESS_TOKEN_LIFESPAN=900

HIBERNATE_SCHEMA_STRATEGY=validate
ELASTIC_LOGGING_ENABLED=false
```

Ces variables sont suffisantes pour démarrer l'API avec PostgreSQL + Redis.

Variables S3/MinIO optionnelles :

```env
AWS_REGION=us-east-1
AWS_CREDENTIALS_TYPE=static
AWS_ACCESS_KEY_ID=<access-key>
AWS_SECRET_ACCESS_KEY=<secret-key>
S3_BUCKET=<bucket>
S3_ENDPOINT_OVERRIDE=<endpoint-si-minio-ou-s3-compatible>
```

Variables SMTP optionnelles, nécessaires pour envoyer de vrais emails :

```env
QUARKUS_MAILER_HOST=<smtp-host>
QUARKUS_MAILER_PORT=587
QUARKUS_MAILER_USERNAME=<smtp-user>
QUARKUS_MAILER_PASSWORD=<smtp-password>
QUARKUS_MAILER_FROM=no-reply@noura-school.com
QUARKUS_MAILER_START_TLS=REQUIRED
```

Ne pas ajouter ces variables Elasticsearch tant que tu n'as pas créé un Elasticsearch :

```env
ELASTIC_HOST=<host-elasticsearch>
ELASTIC_PORT=9200
ELASTIC_INDEX=noura-audit
```

Garder simplement :

```env
ELASTIC_LOGGING_ENABLED=false
```

Bloc complet si tu décides d'activer S3/MinIO et Elasticsearch plus tard :

```env
AWS_REGION=us-east-1
AWS_CREDENTIALS_TYPE=static
AWS_ACCESS_KEY_ID=<access-key>
AWS_SECRET_ACCESS_KEY=<secret-key>
S3_BUCKET=<bucket>
S3_ENDPOINT_OVERRIDE=<endpoint-si-minio-ou-s3-compatible>

ELASTIC_LOGGING_ENABLED=true
ELASTIC_HOST=<host-elasticsearch>
ELASTIC_PORT=9200
ELASTIC_INDEX=noura-audit
```

Recommandations :

- `QUARKUS_DEV_SERVICES_ENABLED=false` évite que Quarkus essaye de lancer des services de dev dans le conteneur.
- `HIBERNATE_SCHEMA_STRATEGY=validate` évite que Hibernate modifie le schéma directement. Flyway doit rester la source de vérité.
- `JWT_ISSUER` doit correspondre à l'URL publique de l'API ou à l'issuer attendu par les clients.
- `CORS_ORIGINS` doit contenir l'URL exacte du frontend. En local avec `noura-school-frontend`, utiliser `http://localhost:4200,http://127.0.0.1:4200`. En staging, utiliser l'URL publique du frontend déployé.
- Redis est nécessaire pour l'authentification sécurisée. Sans Redis, les endpoints de login/reset/OTP risquent d'échouer.
- Elasticsearch n'est pas nécessaire si `ELASTIC_LOGGING_ENABLED=false`; les audits restent persistés en PostgreSQL.
- S3/MinIO n'est pas nécessaire pour le démarrage actuel, car aucun use case métier n'appelle encore `StorageService`.
- SMTP n'est pas obligatoire au démarrage, mais la création d'un élève tente d'envoyer un email avec les identifiants.
- Ne jamais mettre les secrets dans Git. Les valeurs sensibles restent dans Coolify et GitHub Secrets.

### 8. Premier déploiement manuel

Avant d'activer l'automatisation :

1. Dans Coolify, cliquer sur **Deploy**.
2. Suivre les logs de build.
3. Vérifier que Maven termine correctement.
4. Vérifier que l'application démarre.
5. Vérifier les migrations Flyway dans les logs.
6. Tester la readiness :

```bash
curl https://api-dev.noura-school.com/q/health/ready
```

Résultat attendu : statut HTTP `200` et état global `UP`.

Tester aussi l'OpenAPI si exposé :

```bash
curl https://api-dev.noura-school.com/q/openapi
```

En production stricte, Swagger UI n'est pas inclus par `application-prod.yml`, mais `/q/openapi` reste configuré.

### 9. Activer le déploiement depuis GitHub Actions

Le workflow GitHub Actions [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) contient deux jobs :

```text
build
deploy-develop
```

Le job `build` :

1. installe Java 21;
2. lance `mvn verify`;
3. package l'application;
4. build l'image Docker;
5. scanne l'image avec Trivy.

Le job `deploy-develop` :

1. attend que `build` réussisse;
2. ne s'exécute que sur un push vers `develop`;
3. appelle le deploy webhook Coolify avec un token API.

Conséquence : une pull request vers `develop` lance la CI, mais ne déploie pas. Le déploiement se fait après le merge, quand GitHub crée le push sur `develop`.

### 10. Créer le token API Coolify

Dans Coolify :

1. Aller dans **Settings**.
2. Ouvrir **Configuration** puis **Advanced**.
3. Activer **API Access** si ce n'est pas déjà fait.
4. Aller dans **Keys & Tokens**.
5. Ouvrir **API Tokens**.
6. Créer un token avec la permission :

```text
Deploy
```

7. Copier le token immédiatement.

Ce token sera stocké dans GitHub sous le nom :

```text
COOLIFY_TOKEN
```

### 11. Récupérer le deploy webhook Coolify

Dans Coolify :

1. Ouvrir l'application backend.
2. Aller dans la page **Webhook**.
3. Copier le **Deploy webhook**.

Cette URL sera stockée dans GitHub sous le nom :

```text
COOLIFY_WEBHOOK
```

### 12. Ajouter les secrets GitHub

Dans GitHub :

1. Ouvrir le dépôt.
2. Aller dans **Settings**.
3. Aller dans **Secrets and variables** puis **Actions**.
4. Cliquer sur **New repository secret**.
5. Ajouter :

```text
Name: COOLIFY_WEBHOOK
Value: <deploy webhook Coolify>
```

6. Ajouter un second secret :

```text
Name: COOLIFY_TOKEN
Value: <token API Coolify>
```

Le workflow utilise ensuite :

```bash
curl --fail --request GET "${{ secrets.COOLIFY_WEBHOOK }}" \
  --header "Authorization: Bearer ${{ secrets.COOLIFY_TOKEN }}"
```

### 13. Protéger la branche `develop`

Dans GitHub :

1. Aller dans **Settings**.
2. Aller dans **Branches**.
3. Créer ou modifier la règle pour :

```text
develop
```

4. Activer :

```text
Require a pull request before merging
Require status checks to pass before merging
Require branches to be up to date before merging
```

5. Sélectionner le check GitHub Actions correspondant au workflow CI/CD.

Avec cette protection, on évite de merger dans `develop` si les tests, la compilation ou le scan échouent.

### 14. Tester le pipeline complet

Pour valider la chaîne :

1. Créer une petite branche de test.
2. Faire une modification non risquée, par exemple dans la documentation.
3. Ouvrir une PR vers `develop`.
4. Vérifier que GitHub Actions lance le job `build`.
5. Merger la PR quand la CI passe.
6. Aller dans GitHub Actions et vérifier que le push sur `develop` lance :

```text
build -> deploy-develop
```

7. Aller dans Coolify et vérifier qu'un nouveau déploiement est lancé.
8. Après le déploiement, tester :

```bash
curl https://api-dev.noura-school.com/q/health/ready
```

### 15. Dépannage courant

#### Le build Coolify échoue pendant Maven

À vérifier :

1. Le serveur a assez de RAM.
2. Le build utilise bien `docker/Dockerfile`.
3. Coolify a accès à Internet pour Maven Central.
4. La branche configurée est bien `develop`.

#### L'application démarre mais la readiness est DOWN

À vérifier :

1. `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`.
2. Les logs Flyway.
3. La connexion Redis.
4. La valeur de `ELASTIC_LOGGING_ENABLED`. Elle doit rester `false` si aucun Elasticsearch n'est configuré.
5. Les variables SMTP si l'erreur arrive pendant l'envoi d'un email.

#### La CI GitHub passe mais Coolify ne déploie pas

À vérifier :

1. Le job `deploy-develop` apparaît bien dans GitHub Actions.
2. Le workflow est lancé par un push sur `develop`, pas seulement par une PR.
3. `COOLIFY_WEBHOOK` est correct.
4. `COOLIFY_TOKEN` est correct et possède la permission `Deploy`.
5. L'API Coolify est activée.

#### Erreur CORS depuis le frontend

À vérifier :

1. `CORS_ORIGINS` contient exactement l'URL du frontend.
2. Le protocole est correct : `https://` et non `http://`.
3. Pas de slash final si le frontend n'envoie pas l'origine avec slash.

Exemple local avec `noura-school-frontend` :

```env
CORS_ORIGINS=http://localhost:4200,http://127.0.0.1:4200
```

Exemple staging :

```env
CORS_ORIGINS=https://dev.noura-school.com
```

#### Les migrations Flyway échouent

À vérifier :

1. Les scripts dans `src/main/resources/db/migration`.
2. L'ordre des versions `V1`, `V2`, etc.
3. L'état de la table `flyway_schema_history`.
4. Que `HIBERNATE_SCHEMA_STRATEGY=validate` est bien défini pour éviter les modifications automatiques concurrentes.

### 16. Checklist finale

Avant de considérer le déploiement prêt :

- Domaine API pointé vers le serveur Coolify.
- PostgreSQL 16 créé et démarré.
- Redis 7 créé et démarré.
- Elasticsearch non créé et `ELASTIC_LOGGING_ENABLED=false`, sauf besoin explicite.
- Bucket S3 ou MinIO prêt uniquement si les fonctionnalités de stockage sont activées.
- SMTP configuré uniquement si l'envoi réel d'emails doit fonctionner.
- Application Coolify sur branche `develop`.
- Dockerfile configuré sur `docker/Dockerfile`.
- Port `8080` configuré.
- Health check `/q/health/ready` configuré.
- Variables d'environnement Coolify renseignées.
- Premier déploiement manuel réussi.
- `COOLIFY_WEBHOOK` ajouté dans GitHub Secrets.
- `COOLIFY_TOKEN` ajouté dans GitHub Secrets.
- Branch protection activée sur `develop`.
- Merge de test confirmé : GitHub Actions déclenche Coolify après succès.
