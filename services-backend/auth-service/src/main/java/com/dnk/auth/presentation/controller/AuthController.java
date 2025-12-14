package com.dnk.auth.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnk.auth.application.model.AuthTokens;
import com.dnk.auth.application.model.GoogleLoginResult;
import com.dnk.auth.application.usecase.GoogleLoginUseCase;
import com.dnk.auth.application.usecase.VerifyMfaOtpUseCase;
import com.dnk.auth.presentation.auth.GoogleLoginRequest;
import com.dnk.auth.presentation.auth.GoogleLoginResponse;
import com.dnk.auth.presentation.auth.VerifyMfaOtpRequest;
import com.dnk.auth.presentation.auth.VerifyMfaOtpResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final GoogleLoginUseCase googleLoginUseCase;
    private final VerifyMfaOtpUseCase verifyMfaOtpUseCase;

    public AuthController(GoogleLoginUseCase googleLoginUseCase,
                          VerifyMfaOtpUseCase verifyMfaOtpUseCase) {
        this.googleLoginUseCase = googleLoginUseCase;
        this.verifyMfaOtpUseCase = verifyMfaOtpUseCase;
    }

    @PostMapping("/login/google")
    public ResponseEntity<GoogleLoginResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        GoogleLoginResult result = googleLoginUseCase.execute(request.idToken());

        GoogleLoginResponse response = new GoogleLoginResponse(
                result.mfaRequired(),
                result.userId(),
                result.email());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<VerifyMfaOtpResponse> verifyMfa(@Valid @RequestBody VerifyMfaOtpRequest request) {
        AuthTokens tokens = verifyMfaOtpUseCase.execute(request.userId(), request.otp());

        VerifyMfaOtpResponse response = new VerifyMfaOtpResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                "Bearer",
                900L);

        return ResponseEntity.ok(response);
    }
}
