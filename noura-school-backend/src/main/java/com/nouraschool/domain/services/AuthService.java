package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.AuthMeDto;
import com.nouraschool.domain.dtos.LoginRequest;
import com.nouraschool.domain.dtos.LoginResponse;

import java.util.UUID;
import jakarta.ws.rs.core.SecurityContext;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(String refreshTokenValue);

    AuthMeDto me(SecurityContext securityContext);

    void logout(UUID userId);

    /** Envoie un lien de réinitialisation par email (token Redis 1h, notification log). */
    void forgotPassword(String email);

    /** Réinitialise le mot de passe avec le token envoyé par email. */
    void resetPassword(String token, String newPassword);

    /** Change le mot de passe (utilisateur connecté). */
    void changePassword(UUID userId, String oldPassword, String newPassword);
}
