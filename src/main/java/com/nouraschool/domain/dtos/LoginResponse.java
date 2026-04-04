package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
    /** true si l'utilisateur doit changer son mot de passe (task.md UC-AUTH-01). */
    private Boolean passwordChangeRequired;

    public static LoginResponse of(String accessToken, String refreshToken, Long expiresIn, boolean passwordChangeRequired) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .tokenType("Bearer")
                .passwordChangeRequired(passwordChangeRequired)
                .build();
    }
}
