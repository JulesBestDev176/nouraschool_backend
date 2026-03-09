package com.nouraschool.domain.constants;

public final class Constants {

    private Constants() {
    }

    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_PATH = "/api/v1/auth";
    public static final String LOGIN_PATH = "/api/v1/auth/login";
    public static final String REFRESH_PATH = "/api/v1/auth/refresh";
    public static final String LOGOUT_PATH = "/api/v1/auth/logout";
    public static final String ME_PATH = "/api/v1/auth/me";
    public static final String SWAGGER_UI_PATH = "/q/swagger-ui";
    public static final String OPENAPI_PATH = "/q/openapi";
    public static final String HEALTH_PATH = "/health";
    /** Header HTTP pour la résolution du tenant (UUID). */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";
    /** Header HTTP pour l'ID de corrélation (requête / réponse). */
    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CAISSIER = "CAISSIER";
    public static final String ROLE_ENSEIGNANT = "ENSEIGNANT";
    public static final String ROLE_ELEVE = "ELEVE";
    public static final String ROLE_PARENT = "PARENT";
    public static final String ROLE_SURVEILLANT = "SURVEILLANT";
    public static final String ROLE_RH = "RH";
    public static final String[] ALL_ROLES = {
            ROLE_ADMIN, ROLE_CAISSIER, ROLE_RH, ROLE_SURVEILLANT, ROLE_ENSEIGNANT, ROLE_ELEVE, ROLE_PARENT
    };
    /** Longueur minimale du mot de passe (spec auth). */
    public static final int PASSWORD_MIN_LENGTH = 12;
}
