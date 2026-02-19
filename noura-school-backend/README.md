# noura-school-backend

This project uses Quarkus, the Supersonic Subatomic Java Framework.

## Documentation API (Swagger)

- **Swagger UI** : [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui) (en dev)
- **OpenAPI JSON** : [http://localhost:8080/q/openapi](http://localhost:8080/q/openapi)

Authentification : utiliser **Authorize** dans Swagger UI et coller le JWT obtenu via `POST /api/auth/login`.

## Postman – variables pour les tests

Créer un environnement ou une collection avec les variables :

| Variable | Valeur | Usage |
|----------|--------|--------|
| `base_url` | `http://localhost:8080` | URL de base |
| `access_token` | *(vide, rempli après login)* | Header `Authorization: Bearer {{access_token}}` |
| `refresh_token` | *(vide)* | Body de `/api/auth/refresh` |
| `username` | `admin` (ou autre rôle) | Login |
| `password` | mot de passe | Login |
| `id` | `1` | GET/PUT/DELETE par ID |
| `eleveId` | `1` | Endpoints parent par enfant |

**Workflow :** exécuter `POST {{base_url}}/api/auth/login` avec `{"username":"{{username}}","password":"{{password}}"}` puis copier `accessToken` dans `access_token`. Pour enregistrer automatiquement le token, ajouter dans **Tests** de la requête Login :

```js
if (pm.response.code === 200) {
  const j = pm.response.json();
  if (j.accessToken) pm.collectionVariables.set('access_token', j.accessToken);
  if (j.refreshToken) pm.collectionVariables.set('refresh_token', j.refreshToken);
}
```

**Endpoints par rôle :** Admin `/api/admin/*`, Caissier `/api/caisse/*`, Enseignant `/api/enseignant/*`, Élève `/api/eleve/*`, Parent `/api/parent/*`.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/noura-school-backend-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
