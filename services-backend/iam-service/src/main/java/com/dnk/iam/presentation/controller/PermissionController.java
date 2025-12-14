package com.dnk.iam.presentation.controller;

import com.dnk.iam.application.usecase.CreatePermissionUseCase;
import com.dnk.iam.presentation.dto.CreatePermissionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iam/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final CreatePermissionUseCase createPermissionUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        createPermissionUseCase.execute(request.name());
    }
}
