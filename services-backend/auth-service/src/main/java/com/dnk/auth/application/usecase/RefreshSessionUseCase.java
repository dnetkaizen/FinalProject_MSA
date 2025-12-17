package com.dnk.auth.application.usecase;

import com.dnk.auth.application.exception.AuthException;
import com.dnk.auth.application.model.AuthTokens;
import com.dnk.auth.application.port.out.TokenProviderPort;
import com.dnk.auth.infrastructure.persistence.UserRightsFetcher;

import java.util.List;

public class RefreshSessionUseCase {

    private final TokenProviderPort tokenProviderPort;
    private final UserRightsFetcher userRightsFetcher;

    public RefreshSessionUseCase(TokenProviderPort tokenProviderPort,
                                 UserRightsFetcher userRightsFetcher) {
        this.tokenProviderPort = tokenProviderPort;
        this.userRightsFetcher = userRightsFetcher;
    }

    public AuthTokens execute(String refreshToken) {
        if (!tokenProviderPort.validateToken(refreshToken)) {
            throw new AuthException("Invalid refresh token");
        }

        String userId = tokenProviderPort.getUserIdFromToken(refreshToken);
        String email = tokenProviderPort.getEmailFromToken(refreshToken);

        List<String> roles = userRightsFetcher.getUserRoles(userId);
        List<String> permissions = userRightsFetcher.getUserPermissions(userId);

        String newAccessToken = tokenProviderPort.generateAccessToken(userId, email, roles, permissions);
        String newRefreshToken = tokenProviderPort.generateRefreshToken(userId, email);

        return new AuthTokens(newAccessToken, newRefreshToken);
    }
}
