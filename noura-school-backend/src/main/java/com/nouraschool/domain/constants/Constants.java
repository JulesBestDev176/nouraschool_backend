package com.nouraschool.domain.constants;

public final class Constants {

    private Constants() {
    }

    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_PATH = "/api/auth";
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String REFRESH_PATH = "/api/auth/refresh";
    public static final String LOGOUT_PATH = "/api/auth/logout";
    public static final String SWAGGER_UI_PATH = "/q/swagger-ui";
    public static final String OPENAPI_PATH = "/q/openapi";
    public static final String HEALTH_PATH = "/health";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CAISSIER = "CAISSIER";
    public static final String ROLE_ENSEIGNANT = "ENSEIGNANT";
    public static final String ROLE_ELEVE = "ELEVE";
    public static final String ROLE_PARENT = "PARENT";
    public static final String[] ALL_ROLES = {
            ROLE_ADMIN, ROLE_CAISSIER, ROLE_ENSEIGNANT, ROLE_ELEVE, ROLE_PARENT
    };
}
