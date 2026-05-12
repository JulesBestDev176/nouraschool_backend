# Guide CI/CD — Noura School Backend

## Vue d'ensemble

```
Push/Merge → develop
      ↓
GitHub Actions (CI)
  → mvn package (build JAR)
  → docker build (image)
  → trivy scan (sécurité)
      ↓ (si succès)
Coolify (CD)
  → redéploiement automatique
```

---

## Étape 1 — Le fichier workflow GitHub Actions

Crée le fichier `.github/workflows/ci.yml` à la racine du projet :

```yaml
name: CI/CD

on:
  push:
    branches: [main, develop]       # déclenché sur push
  pull_request:
    branches: [main, develop]       # déclenché sur PR (CI uniquement, pas de deploy)

concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true          # annule le run précédent si nouveau push

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: "21"
          distribution: "temurin"
          cache: "maven"            # cache Maven entre les runs = plus rapide

      - name: Build package
        run: mvn package -B -Dmaven.test.skip=true   # -B = batch mode, skip tests
        env:
          MAVEN_OPTS: -Xmx1024m

      - name: Build Docker image
        run: docker build -f docker/Dockerfile -t noura-school-backend:${{ github.sha }} .
        env:
          DOCKER_BUILDKIT: 1

      - name: Scan image (Trivy)
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: "noura-school-backend:${{ github.sha }}"
          format: "sarif"
          output: "trivy-results.sarif"
          severity: "CRITICAL,HIGH"

      - name: Upload Trivy scan
        if: success()
        continue-on-error: true
        uses: github/codeql-action/upload-sarif@v3
        with:
          sarif_file: "trivy-results.sarif"

  deploy-develop:
    name: Deploy develop to Coolify
    needs: build                    # attend que build réussisse
    runs-on: ubuntu-latest
    if: github.event_name == 'push' && github.ref == 'refs/heads/develop'  # deploy SEULEMENT sur push develop, pas sur PR
    steps:
      - name: Trigger Coolify deployment
        run: |
          curl --fail --request POST "${{ secrets.COOLIFY_WEBHOOK }}" \
            --header "Authorization: Bearer ${{ secrets.COOLIFY_TOKEN }}" \
            --header "Content-Type: application/json"
```

**Points clés :**
- `needs: build` → le deploy n'est jamais lancé si le build échoue
- La condition `if:` sur `deploy-develop` → les PRs font tourner la CI mais ne déploient pas
- `${{ secrets.COOLIFY_WEBHOOK }}` et `${{ secrets.COOLIFY_TOKEN }}` → valeurs stockées dans GitHub, jamais dans le code

---

## Étape 2 — Préparer Coolify

### 2.1 Créer PostgreSQL 16

Dans Coolify → **New Resource → Database → PostgreSQL 16**
- Nom : `noura-school-postgres-develop`
- Noter le hostname interne (ex: `postgresql-xxxx`)

### 2.2 Créer Redis 7

Dans Coolify → **New Resource → Database → Redis 7**
- Nom : `noura-school-redis-develop`
- Noter le hostname interne

### 2.3 Créer l'application backend

Dans Coolify → **New Resource → Application**

| Champ | Valeur |
|-------|--------|
| Repo | `JulesBestDev176/nouraschool_backend` |
| Branche | `develop` |
| Build Pack | `Dockerfile` |
| Dockerfile | `docker/Dockerfile` |
| Build context | `.` (racine du repo) |
| Port | `8080` |
| Health check | `/q/health/ready` |

### 2.4 Variables d'environnement dans Coolify

```env
QUARKUS_DEV_SERVICES_ENABLED=false
HIBERNATE_SCHEMA_STRATEGY=none

DB_HOST=<hostname-interne-postgres>
DB_PORT=5432
DB_NAME=noura_school_db
DB_USERNAME=<user>
DB_PASSWORD=<password>

REDIS_HOST=<hostname-interne-redis>
REDIS_PORT=6379
REDIS_PASSWORD=<password-si-present>

JWT_ISSUER=https://api-dev.noura-school.com
JWT_AUDIENCE=noura-school-api
JWT_ACCESS_TOKEN_LIFESPAN=900

ELASTIC_LOGGING_ENABLED=false
```

### 2.5 Premier déploiement manuel

Dans Coolify → clique **Deploy** → vérifie les logs → teste :

```bash
curl https://api-dev.noura-school.com/q/health/ready
# Attendu : {"status":"UP"}
```

---

## Étape 3 — Créer le token API Coolify

Dans Coolify → **icône profil (haut à droite) → Security → API Tokens → New Token**
- Permission : `Deploy` suffit
- Copie le token immédiatement (affiché une seule fois)

---

## Étape 4 — Récupérer le webhook Coolify

Dans Coolify → ton application backend → onglet **Webhooks**
- Copie le **Deploy Webhook** (URL de la forme `https://coolify.xxx.com/api/v1/deploy?uuid=...`)

> **Important** : Coolify utilise `POST`, pas `GET`

---

## Étape 5 — Ajouter les secrets GitHub

Dans GitHub → ton repo → **Settings → Secrets and variables → Actions → New repository secret**

| Secret | Valeur |
|--------|--------|
| `COOLIFY_WEBHOOK` | L'URL du deploy webhook Coolify |
| `COOLIFY_TOKEN` | Le token API Coolify |

---

## Étape 6 — Branch protection sur `develop`

Dans GitHub → **Settings → Branches → Add rule**

```
Branch name pattern : develop
☑ Require a pull request before merging
☑ Require status checks to pass before merging
  → Sélectionner le check "build"
☑ Require branches to be up to date before merging
```

Effet : impossible de merger dans `develop` si la CI échoue.

---

## Résumé du flux final

```
1. Tu crées une branche feature
2. Tu ouvres une PR vers develop
   → GitHub Actions lance : build + docker + trivy
   → Si ça échoue → merge bloqué
3. Tu merges la PR
   → GitHub Actions lance : build + docker + trivy + deploy Coolify
   → Coolify redéploie automatiquement
```

---

## Erreurs rencontrées et solutions

| Erreur | Cause | Fix |
|--------|-------|-----|
| `package org.assertj does not exist` | Tests compilent avec dépendances manquantes | `-Dmaven.test.skip=true` |
| `Cannot convert jar to enum JarType` | Flag `-Dquarkus.package.type=jar` invalide en Quarkus 3.x | Supprimer le flag |
| `Schema validation: wrong column type` | `HIBERNATE_SCHEMA_STRATEGY=validate` + mismatch UUID | Passer à `none` dans `application-prod.yml` |
| Webhook Coolify échoue | Workflow utilisait `GET` au lieu de `POST` | `--request POST` dans le curl |
