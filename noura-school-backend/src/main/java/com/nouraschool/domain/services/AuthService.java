package com.nouraschool.domain.services;

import com.nouraschool.domain.dtos.LoginRequest;
import com.nouraschool.domain.dtos.LoginResponse;

import java.util.UUID;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(String refreshTokenValue);

    void logout(UUID userId);
}
