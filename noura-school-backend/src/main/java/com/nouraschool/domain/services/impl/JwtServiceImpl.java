package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.services.JwtService;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;

@ApplicationScoped
public class JwtServiceImpl implements JwtService {

    private final JWTParser jwtParser;

    @Inject
    public JwtServiceImpl(JWTParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    public boolean isValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            jwtParser.parse(token);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public Optional<JsonWebToken> parse(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            JsonWebToken jwt = jwtParser.parse(token);
            return Optional.of(jwt);
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}
