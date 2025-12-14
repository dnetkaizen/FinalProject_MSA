package com.dnk.iam.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Permission(UUID id, String name) {

    public Permission {
        Objects.requireNonNull(id, "id must not be null");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Permission name must not be empty");
        }
    }
}
