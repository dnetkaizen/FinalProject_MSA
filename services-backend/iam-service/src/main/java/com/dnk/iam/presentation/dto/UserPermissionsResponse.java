package com.dnk.iam.presentation.dto;

import java.util.List;

public record UserPermissionsResponse(
    List<String> permissions
) {
    public static UserPermissionsResponse from(List<String> permissions) {
        return new UserPermissionsResponse(permissions);
    }
}
