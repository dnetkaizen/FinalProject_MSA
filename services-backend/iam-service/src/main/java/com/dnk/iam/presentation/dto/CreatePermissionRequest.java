package com.dnk.iam.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(
    @NotBlank(message = "Permission name is required")
    String name
) {}
