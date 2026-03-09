# Monitoring — Noura School Backend

## Health checks

- **Liveness** : `GET /q/health/live` — application vivante
- **Readiness** : `GET /q/health/ready` — prête à recevoir du trafic

## Alertes recommandées (task.md FOLDER 13)

| Métrique          | Seuil  | Action                |
|-------------------|--------|-----------------------|
| 5xx rate          | > 1%   | Alerte, investigation |
| Latence p95       | > 2s   | Alerte                |
| Erreurs connexion | > 0    | Alerte critique       |
| Memory > 80%      |        | Alerte                |
| CPU > 90%         |        | Alerte                |

## Intégration Prometheus / Grafana

Quarkus expose les métriques via `quarkus-micrometer-registry-prometheus` si activé.
Endpoint : `GET /q/metrics`

## Logs

- Profil prod : logs JSON (`quarkus.log.console.json=true`)
- Rechercher 5xx : `grep '"status":5' logs.json`
- Latence : métriques HTTP dans `/q/metrics`
