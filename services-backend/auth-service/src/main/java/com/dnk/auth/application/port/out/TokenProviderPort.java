package com.dnk.auth.application.port.out;

public interface TokenProviderPort {

    String generateAccessToken(String userId, String email);

    String generateRefreshToken(String userId, String email);
}
