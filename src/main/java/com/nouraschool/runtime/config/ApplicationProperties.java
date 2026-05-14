package com.nouraschool.runtime.config;

import com.nouraschool.domain.exception.codes.BackendError;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ApplicationProperties {

    @ConfigProperty(name = "app.jwt.issuer", defaultValue = "https://noura-school.com")
    String jwtIssuer;

    @ConfigProperty(name = "app.jwt.audience", defaultValue = "noura-school-api")
    String jwtAudience;

    @ConfigProperty(name = "app.jwt.access-token-lifespan", defaultValue = "900")
    long accessTokenLifespan;

    @ConfigProperty(name = "app.security.jwt-validation-enabled", defaultValue = "true")
    boolean jwtValidationEnabled;

    @ConfigProperty(name = "app.security.clock-skew-seconds", defaultValue = "60")
    int clockSkewSeconds;

    /** Codes d'erreur métier (remplace le CSV) — source : application.yml app.error-codes, défauts ci-dessous. */
    private final Map<String, BackendError> errorByCode = new HashMap<>();

    @PostConstruct
    void initErrorCodes() {
        add("IDENTIFIANTS_INVALIDES", 401, "Identifiants invalides");
        add("COMPTE_VERROUILLE", 423, "Compte temporairement verrouillé");
        add("TOKEN_EXPIRE", 401, "Token expiré");
        add("ACCES_REFUSE", 403, "Accès refusé");
        add("RESSOURCE_INTROUVABLE", 404, "Ressource introuvable");
        add("EMAIL_DEJA_UTILISE", 409, "Cet email est déjà utilisé");
        add("VALIDATION_ECHOUEE", 400, "Contraintes de validation non respectées");
        add("REGLE_METIER_VIOLEE", 422, "Règle métier non respectée");
        add("TENANT_INACTIF", 403, "Établissement suspendu");
        add("OTP_INVALIDE", 401, "Code OTP incorrect ou expiré");
        add("TELEPHONE_PARENT_REQUIS", 400, "Le parent doit avoir un numéro WhatsApp valide");
        add("ERREUR_INTERNE", 500, "Une erreur inattendue s'est produite");
        add("USER_INACTIVE", 403, "Compte inactif");
        add("PARENT_LOGIN_DISABLED", 403, "Connexion parent désactivée (accès OTP uniquement)");
        add("REFRESH_TOKEN_INVALID", 401, "Token de rafraîchissement invalide ou révoqué");
        add("REFRESH_TOKEN_EXPIRED", 401, "Token de rafraîchissement expiré");
        add("NOT_FOUND", 404, "Ressource introuvable");
        add("INVALID_CREDENTIALS", 401, "Identifiants invalides");
        add("RESET_TOKEN_INVALIDE", 400, "Lien de réinitialisation invalide ou expiré");
        add("MOT_DE_PASSE_ACTUEL_INCORRECT", 401, "Mot de passe actuel incorrect");
        add("MOT_DE_PASSE_TROP_COURT", 400, "Le mot de passe doit contenir au moins 12 caractères");
        add("TOO_MANY_REQUESTS", 429, "Trop de tentatives, réessayez plus tard");
    }

    private void add(String code, int httpCode, String message) {
        BackendError e = new BackendError();
        e.setInternalNameCode(code);
        e.setInternalCode(httpCode);
        e.setHttpCode(httpCode);
        e.setInternalMessage(message);
        errorByCode.put(code, e);
    }

    public BackendError getErrorByCode(String code) {
        return errorByCode.get(code);
    }

    public JwtConfig jwt() {
        return new JwtConfig(jwtIssuer, jwtAudience, accessTokenLifespan);
    }

    public SecurityConfig security() {
        return new SecurityConfig(jwtValidationEnabled, clockSkewSeconds);
    }

    public record JwtConfig(String issuer, String audience, long accessTokenLifespan) {
    }

    public record SecurityConfig(boolean jwtValidationEnabled, int clockSkewSeconds) {
    }
}
