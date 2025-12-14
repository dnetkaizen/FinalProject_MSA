package com.dnk.iam.presentation.controller;

import com.dnk.iam.application.usecase.AssignPermissionToRoleUseCase;
import com.dnk.iam.presentation.dto.AssignPermissionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iam/roles/{roleName}/permissions")
@RequiredArgsConstructor
@Validated
public class RolePermissionController {

    private final AssignPermissionToRoleUseCase assignPermissionToRoleUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignPermissionToRole(
            @PathVariable @NotBlank String roleName,
            @Valid @RequestBody AssignPermissionRequest request
    ) {
        assignPermissionToRoleUseCase.execute(roleName, request.permission());
    }
}
