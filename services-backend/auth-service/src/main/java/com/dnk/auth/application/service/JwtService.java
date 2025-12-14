package com.dnk.auth.application.service;

import com.dnk.auth.application.port.out.TokenProviderPort;

public class JwtService {

    private final TokenProviderPort tokenProviderPort;

    public JwtService(TokenProviderPort tokenProviderPort) {
        this.tokenProviderPort = tokenProviderPort;
    }

    public String generateAccessToken(String userId, String email) {
        return tokenProviderPort.generateAccessToken(userId, email);
    }

    public String generateRefreshToken(String userId, String email) {
        return tokenProviderPort.generateRefreshToken(userId, email);
    }
}
