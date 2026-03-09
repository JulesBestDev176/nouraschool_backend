package com.nouraschool.runtime.config;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.info.Contact;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.info.License;
import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;
import org.eclipse.microprofile.openapi.models.servers.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@OpenApiFilter(OpenApiFilter.RunStage.RUN)
public class SwaggerConfig implements OASFilter, ConfigSource {

    private static final Map<String, String> CONFIGURATION = new HashMap<>();

    static {
        CONFIGURATION.put("quarkus.swagger-ui.path", "/q/swagger-ui");
        // always-include contrôlé par profil : true en dev (application-dev.yml), false en prod (application-prod.yml)
        CONFIGURATION.put("quarkus.smallrye-openapi.path", "/q/openapi");
    }

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        openAPI.setInfo(createInfo());
        openAPI.setServers(List.of(
                createServer("http://localhost:8080", "Développement local"),
                createServer("https://api.noura-school.com", "Production")
        ));
        openAPI.addSecurityRequirement(createSecurityRequirement("Bearer"));
        var components = openAPI.getComponents();
        if (components == null) {
            components = OASFactory.createObject(org.eclipse.microprofile.openapi.models.Components.class);
        }
        components.addSecurityScheme("Bearer", createBearerScheme());
        openAPI.setComponents(components);
    }

    private Info createInfo() {
        Contact contact = OASFactory.createContact()
                .name("Noura School")
                .url("https://www.noura-school.com")
                .email("support@noura-school.com");
        License license = OASFactory.createLicense()
                .name("Proprietary")
                .url("https://www.noura-school.com/license");
        return OASFactory.createInfo()
                .title("Noura School API")
                .description("API REST pour la gestion scolaire.\n\n" +
                        "## Fonctionnalités\n" +
                        "- **Authentification** : Login, refresh token, JWT\n" +
                        "- **Gestion des élèves** : Inscription, bulletins, absences\n" +
                        "- **Gestion des enseignants** : Emplois du temps, matières\n" +
                        "- **Paiements** : Suivi des frais scolaires\n" +
                        "- **Notifications** : Alertes et communications\n\n" +
                        "## Authentification\n" +
                        "- **Bearer JWT** : Header Authorization requis pour les endpoints protégés\n" +
                        "- Obtenir un token via POST /api/auth/login")
                .version("1.0.0")
                .contact(contact)
                .license(license);
    }

    private Server createServer(String url, String description) {
        return OASFactory.createServer().url(url).description(description);
    }

    private SecurityRequirement createSecurityRequirement(String name) {
        SecurityRequirement req = OASFactory.createObject(SecurityRequirement.class);
        req.addScheme(name);
        return req;
    }

    private SecurityScheme createBearerScheme() {
        return OASFactory.createObject(SecurityScheme.class)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT (RS256). Ex: Authorization: Bearer <token>");
    }

    @Override
    public int getOrdinal() {
        return 100;
    }

    @Override
    public Set<String> getPropertyNames() {
        return CONFIGURATION.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return CONFIGURATION.get(propertyName);
    }

    @Override
    public String getName() {
        return SwaggerConfig.class.getSimpleName();
    }
}
