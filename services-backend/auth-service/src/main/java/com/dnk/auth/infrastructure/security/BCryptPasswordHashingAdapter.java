package com.dnk.auth.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.dnk.auth.application.port.out.PasswordHashingPort;

@Component
public class BCryptPasswordHashingAdapter implements PasswordHashingPort {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordHashingAdapter() {
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public String hash(String raw) {
        return encoder.encode(raw);
    }

    @Override
    public boolean matches(String raw, String hash) {
        return encoder.matches(raw, hash);
    }
}
