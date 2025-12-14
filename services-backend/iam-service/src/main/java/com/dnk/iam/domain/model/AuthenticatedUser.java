package com.dnk.iam.domain.model;

public record AuthenticatedUser(String userId, String email) {
    
    public AuthenticatedUser {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }
}
