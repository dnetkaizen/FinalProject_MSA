package com.dnk.enrollment.domain.model;

import java.security.Principal;
import java.util.List;
import java.util.Collections;

public record AuthenticatedUser(
    String userId, 
    String email, 
    List<String> roles, 
    List<String> permissions
) implements Principal {

    public AuthenticatedUser {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (roles == null) roles = Collections.emptyList();
        if (permissions == null) permissions = Collections.emptyList();
    }

    @Override
    public String getName() {
        return userId;
    }
}
