package com.dnk.iam.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(
    @NotBlank(message = "Role name is required") 
    String name
) {}
