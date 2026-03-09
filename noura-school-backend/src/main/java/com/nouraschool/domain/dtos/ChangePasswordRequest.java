package com.nouraschool.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Mot de passe actuel requis")
    private String oldPassword;
    @NotBlank(message = "Nouveau mot de passe requis")
    private String newPassword;
}
