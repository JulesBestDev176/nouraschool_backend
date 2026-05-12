package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.entities.PlateformeUtilisateurEntity;
import com.nouraschool.domain.entities.UserEntity;
import com.nouraschool.domain.services.JwtGenerator;
import com.nouraschool.runtime.config.ApplicationProperties;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;

@ApplicationScoped
public class JwtGeneratorImpl implements JwtGenerator {

    @Inject
    ApplicationProperties applicationProperties;

    @Override
    public String generateAccessToken(UserEntity user, Set<String> roles, long lifespan) {
        var jwt = Jwt.upn(user.username)
                .claim("sub", user.id.toString())
                .claim("userId", user.id.toString())
                .claim("accountType", "TENANT")
                .groups(roles)
                .issuer(applicationProperties.jwt().issuer())
                .audience(applicationProperties.jwt().audience())
                .expiresIn((int) lifespan);

        if (user.tenant != null && user.tenant.id != null) {
            jwt.claim("tenantId", user.tenant.id.toString());
        }

        return jwt.sign();
    }

    @Override
    public String generatePlatformAccessToken(PlateformeUtilisateurEntity user, Set<String> roles, long lifespan) {
        return Jwt.upn(user.email)
                .claim("sub", user.id.toString())
                .claim("userId", user.id.toString())
                .claim("accountType", "PLATFORM")
                .groups(roles)
                .issuer(applicationProperties.jwt().issuer())
                .audience(applicationProperties.jwt().audience())
                .expiresIn((int) lifespan)
                .sign();
    }
}
