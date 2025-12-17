package com.dnk.auth.application.port.out;

import java.util.List;

public interface TokenProviderPort {
    String generateAccessToken(String userId, String email, List<String> roles, List<String> permissions);

    String generateRefreshToken(String userId, String email);

    boolean validateToken(String token);

    String getUserIdFromToken(String token);
    
    String getEmailFromToken(String token);
}
