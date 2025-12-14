package com.dnk.iam.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Role(UUID id, String name) {

    public Role {
        Objects.requireNonNull(id, "id must not be null");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name must not be empty");
        }
    }
}
