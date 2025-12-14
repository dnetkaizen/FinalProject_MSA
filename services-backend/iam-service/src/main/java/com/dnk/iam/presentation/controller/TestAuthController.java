package com.dnk.iam.presentation.controller;

import com.dnk.iam.domain.model.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestAuthController {

    @GetMapping("/me")
    public String getCurrentUser(@AuthenticationPrincipal AuthenticatedUser user) {
        return String.format("User ID: %s, Email: %s", user.userId(), user.email());
    }
}
