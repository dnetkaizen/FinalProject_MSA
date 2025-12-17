package com.dnk.iam.presentation.dto;

import com.dnk.iam.domain.model.Role;

import java.util.List;

public record UserRolesResponse(
        String userId,
        List<String> roles) {
    public static UserRolesResponse from(String userId, List<Role> roles) {
        List<String> roleNames = roles.stream()
                .map(Role::name)
                .toList();
        return new UserRolesResponse(userId, roleNames);
    }
}
