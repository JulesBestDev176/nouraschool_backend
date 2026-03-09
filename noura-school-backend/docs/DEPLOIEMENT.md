# Guide de déploiement — Noura School Backend

## Prérequis

- Java 21
- PostgreSQL 16
- Redis 7+
- S3-compatible storage (ou MinIO en staging)

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

### S3
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `S3_BUCKET`

### Optionnel
- `ELASTIC_LOGGING_ENABLED` — activer logs Elasticsearch
- `ELASTIC_HOST`, `ELASTIC_PORT`, `ELASTIC_INDEX`

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
