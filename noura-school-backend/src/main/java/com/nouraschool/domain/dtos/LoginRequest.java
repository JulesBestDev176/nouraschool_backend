package com.nouraschool.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    /** Email, username ou numéro de téléphone (ex. +221771234567). Accepte "login" ou "username" en JSON. */
    @NotBlank(message = "Email, identifiant ou numéro de téléphone requis")
    @JsonProperty(value = "login", access = JsonProperty.Access.READ_WRITE)
    @com.fasterxml.jackson.annotation.JsonAlias("username")
    private String login;
    @NotBlank(message = "Mot de passe requis")
    private String password;
}
