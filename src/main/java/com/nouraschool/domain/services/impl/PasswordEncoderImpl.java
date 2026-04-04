package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.services.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class PasswordEncoderImpl implements PasswordEncoder {

    @Inject
    @ConfigProperty(name = "app.bcrypt.cost", defaultValue = "12")
    int bcryptCost;

    @Override
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(bcryptCost));
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
