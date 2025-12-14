package com.dnk.iam.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignPermissionRequest(
    @NotBlank(message = "Permission name is required")
    String permission
) {}
