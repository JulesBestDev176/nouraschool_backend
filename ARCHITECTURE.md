# Architecture Noura School Backend

## Structure des packages

```
com.nouraschool
├── domain                    # Couche domaine
│   ├── constants             # Constantes (ApiConstants)
│   ├── dtos                  # Data Transfer Objects
│   ├── entities              # Entités JPA
│   ├── enums                 # Énumérations
│   ├── exception             # Exceptions et gestion
│   │   ├── codes             # ErrorDto, BackendError
│   │   ├── errors            # InvalidRequestException, ServiceException, NotFoundException
│   │   └── (résolution codes : ConfigBackendErrorResolver dans runtime/config)
│   ├── mappers               # Un mapper par entité (UserMapper, ClasseMapper, etc.)
│   ├── repositories          # Interfaces repository
│   │   └── impl              # Implémentations
│   ├── services              # Services techniques (auth, security, export)
│   │   └── impl              # AuthService, JwtService, PasswordEncoder, TokenHasher, BulletinExportService
│   ├── usecases              # Use cases (logique métier par cas d’usage)
│   │   ├── admin             # Admin*UseCase : CRUD admin (élèves, classes, bulletins, etc.)
│   │   ├── caisse             # CaisseUseCase
│   │   ├── eleve              # EleveUseCase
│   │   ├── enseignant         # EnseignantUseCase
│   │   └── parent             # ParentUseCase
│   └── utils                 # Utilitaires
└── runtime                   # Couche runtime
    ├── aop                   # Intercepteurs CDI (@Logged, @Timed)
    ├── config                # Configuration (ApplicationProperties, Swagger, JWT)
    │   └── impl              # SmallRyeJwtGenerator
    ├── controller            # Ressources REST
    └── security              # Filtres (JwtValidationFilter)
```

## Intercepteurs AOP (équivalent AspectJ)

Quarkus utilise les intercepteurs CDI pour les préoccupations transversales:

- **@Logged** - Logging des entrées/sorties de méthodes
- **@Timed** - Mesure du temps d'exécution (alerte si > 1s)

Utilisation: annoter les classes ou méthodes avec `@Logged` et/ou `@Timed`.

## Flux de requête

1. JwtValidationFilter - Validation JWT pour /api/*
2. Quarkus Security - Authentification JWT
3. AuthResource / Controllers - Traitement REST
4. Use cases - Logique métier (les anciennes classes « service » admin sont des use cases)
5. Repositories - Persistance
6. DefaultExceptionHandler - Gestion des erreurs
