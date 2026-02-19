# Postman – Noura School API

Collection à jour avec **tous les endpoints** de l’application et utilisation des **variables Postman**.

## Variables (collection + environnement)

| Variable        | Description                         | Exemple / Valeurs possibles   |
|-----------------|-------------------------------------|--------------------------------|
| `base_url`      | URL de base de l’API                | `http://localhost:8080`       |
| `access_token`  | JWT (rempli automatiquement après Login) | *(vide au départ)*      |
| `refresh_token` | Token de rafraîchissement           | *(vide au départ)*             |
| `username`      | Identifiant pour login             | `admin`                        |
| `password`      | Mot de passe                       | `admin`                        |
| `id`            | ID générique pour GET/PUT/DELETE   | `1`                            |
| `eleveId`       | ID élève (endpoints Parent)        | `1`                            |
| `noteId`        | ID note (Enseignant – modifier note) | `1`                          |
| `statut`        | Statut paiement (Caisse)           | `EN_ATTENTE`, `VALIDE`, `ECHOUE`, `REMBOURSE` |

## Fichiers

- **Noura-School-API.postman_collection.json** : collection avec tous les endpoints, auth Bearer au niveau collection, variables et scripts de test (Login/Refresh enregistrent les tokens).
- **Noura-School.postman_environment.json** / **environment-local.json** : environnements avec les mêmes variables (à importer et sélectionner dans Postman).

## Workflow

1. Importer la collection et un environnement (ex. *Noura School - Local*).
2. Sélectionner l’environnement dans le sélecteur d’environnement Postman.
3. Exécuter **Auth > Login** : les variables `access_token` et `refresh_token` sont mises à jour automatiquement par le script de test.
4. Les autres requêtes utilisent **Bearer {{access_token}}** (défini au niveau de la collection). Login et Refresh sont en « No Auth ».

## Structure de la collection

| Dossier              | Rôle(s)   | Endpoints principaux                                      |
|----------------------|-----------|-----------------------------------------------------------|
| **Auth**             | Public    | Login, Refresh, Logout                                    |
| **Health**           | Public    | GET /health                                               |
| **Admin - Users**    | ADMIN     | CRUD utilisateurs                                         |
| **Admin - Élèves**   | ADMIN     | CRUD élèves (pagination : page, size, sortBy, asc)        |
| **Admin - Classes**  | ADMIN     | CRUD classes                                              |
| **Admin - Enseignants** | ADMIN  | CRUD enseignants                                          |
| **Admin - Parents**  | ADMIN     | CRUD parents                                              |
| **Admin - Matières** | ADMIN     | CRUD matières                                             |
| **Admin - Matières-Classes** | ADMIN | CRUD affectations matière/classe/enseignant        |
| **Admin - Calendrier scolaire** | ADMIN | CRUD événements calendrier                    |
| **Admin - Emplois du temps** | ADMIN | CRUD emplois du temps                           |
| **Admin - Bulletins**| ADMIN     | CRUD bulletins + download (format: pdf, word, excel)      |
| **Admin - Notes**    | ADMIN     | CRUD notes                                                |
| **Admin - Absences élèves** | ADMIN | CRUD absences élèves                            |
| **Admin - Réclamations** | ADMIN | CRUD réclamations                              |
| **Admin - Paiements**| ADMIN     | CRUD paiements                                            |
| **Caisse**           | CAISSIER  | Liste/statut/historique/consulter/valider paiements       |
| **Enseignant**       | ENSEIGNANT| Profil, classes-matieres, emploi du temps, notes, bulletins, absences, réclamations |
| **Élève**            | ELEVE     | Profil, notes, bulletins, emploi du temps, absences, réclamations, notifications |
| **Parent**           | PARENT    | Profil, enfants, notes/bulletins/absences/emploi par enfant, paiements, notifications |

## Rôles et préfixes API

- **ADMIN** : `/api/admin/*`
- **CAISSIER** : `/api/caisse/*`
- **ENSEIGNANT** : `/api/enseignant/*`
- **ELEVE** : `/api/eleve/*`
- **PARENT** : `/api/parent/*`

Utiliser un compte du rôle voulu (après Login) pour tester les dossiers correspondants.
