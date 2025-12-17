package com.dnk.iam.presentation.controller;

import com.dnk.iam.application.usecase.AssignRoleToUserUseCase;
import com.dnk.iam.application.usecase.GetUserPermissionsUseCase;
import com.dnk.iam.application.usecase.GetUserRolesUseCase;
import com.dnk.iam.domain.model.Role;
import com.dnk.iam.presentation.dto.AssignRoleRequest;
import com.dnk.iam.presentation.dto.UserPermissionsResponse;
import com.dnk.iam.presentation.dto.UserRolesResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iam/users/{userId}/roles")
@RequiredArgsConstructor
@Validated
public class UserRoleController {

    private final AssignRoleToUserUseCase assignRoleToUserUseCase;
    private final GetUserPermissionsUseCase getUserPermissionsUseCase;
    private final GetUserRolesUseCase getUserRolesUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignRoleToUser(
            @PathVariable @NotBlank String userId,
            @Valid @RequestBody AssignRoleRequest request) {
        assignRoleToUserUseCase.execute(userId, request.role());
    }

    @GetMapping
    public UserRolesResponse listUserRoles(
            @PathVariable @NotBlank String userId) {
        List<Role> roles = getUserRolesUseCase.execute(userId);
        return UserRolesResponse.from(userId, roles);
    }

    @GetMapping("/permissions")
    public UserPermissionsResponse getUserPermissions(
            @PathVariable @NotBlank String userId) {
        List<String> permissions = getUserPermissionsUseCase.execute(userId);
        return UserPermissionsResponse.from(permissions);
    }
}
