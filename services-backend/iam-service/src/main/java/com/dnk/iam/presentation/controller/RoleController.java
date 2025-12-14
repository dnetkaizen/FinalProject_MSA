package com.dnk.iam.presentation.controller;

import com.dnk.iam.application.usecase.CreateRoleUseCase;
import com.dnk.iam.presentation.dto.CreateRoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iam/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRole(@Valid @RequestBody CreateRoleRequest request) {
        createRoleUseCase.execute(request.name());
    }
}
