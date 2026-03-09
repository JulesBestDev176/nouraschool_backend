package com.nouraschool.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "Token requis")
    private String token;
    @NotBlank(message = "Nouveau mot de passe requis")
    private String newPassword;
}
