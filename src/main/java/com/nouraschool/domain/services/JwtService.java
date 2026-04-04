package com.nouraschool.domain.services;

import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;

public interface JwtService {

    boolean isValid(String token);

    Optional<JsonWebToken> parse(String token);
}
