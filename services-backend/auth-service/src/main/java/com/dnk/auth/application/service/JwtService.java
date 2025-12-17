package com.dnk.auth.application.service;

import com.dnk.auth.application.port.out.TokenProviderPort;

import java.util.Collections;

public class JwtService {

    private final TokenProviderPort tokenProviderPort;

    public JwtService(TokenProviderPort tokenProviderPort) {
        this.tokenProviderPort = tokenProviderPort;
    }

    public String generateAccessToken(String userId, String email) {
        return tokenProviderPort.generateAccessToken(userId, email, Collections.emptyList(), Collections.emptyList());
    }

    public String generateRefreshToken(String userId, String email) {
        return tokenProviderPort.generateRefreshToken(userId, email);
    }
}
