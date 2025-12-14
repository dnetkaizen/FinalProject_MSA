package com.dnk.iam.domain.model;

import java.util.Objects;
import java.util.UUID;

public record UserRole(String userId, UUID roleId) {

    public UserRole {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(roleId, "roleId must not be null");
    }
}
