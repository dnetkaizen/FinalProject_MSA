package com.dnk.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dnk.auth.application.port.out.EmailSenderPort;
import com.dnk.auth.application.port.out.IdentityProviderPort;
import com.dnk.auth.application.port.out.MfaOtpRepositoryPort;
import com.dnk.auth.application.port.out.PasswordHashingPort;
import com.dnk.auth.application.port.out.TokenProviderPort;
import com.dnk.auth.application.service.JwtService;
import com.dnk.auth.application.usecase.EmailOtpService;
import com.dnk.auth.application.usecase.GoogleLoginUseCase;
import com.dnk.auth.application.usecase.VerifyMfaOtpUseCase;

import com.dnk.auth.infrastructure.persistence.UserRightsFetcher;

import com.dnk.auth.application.usecase.RefreshSessionUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public EmailOtpService emailOtpService(EmailSenderPort emailSenderPort) {
        return new EmailOtpService(emailSenderPort);
    }

    @Bean
    public GoogleLoginUseCase googleLoginUseCase(IdentityProviderPort identityProviderPort,
                                                 MfaOtpRepositoryPort mfaOtpRepositoryPort,
                                                 EmailOtpService emailOtpService,
                                                 UserRightsFetcher userRightsFetcher) {
        return new GoogleLoginUseCase(identityProviderPort, mfaOtpRepositoryPort, emailOtpService, userRightsFetcher);
    }

    @Bean
    public VerifyMfaOtpUseCase verifyMfaOtpUseCase(MfaOtpRepositoryPort mfaOtpRepositoryPort,
                                                   TokenProviderPort tokenProviderPort,
                                                   PasswordHashingPort passwordHashingPort,
                                                   UserRightsFetcher userRightsFetcher) {
        return new VerifyMfaOtpUseCase(mfaOtpRepositoryPort, tokenProviderPort, passwordHashingPort, userRightsFetcher);
    }

    @Bean
    public JwtService jwtService(TokenProviderPort tokenProviderPort) {
        return new JwtService(tokenProviderPort);
    }

    @Bean
    public RefreshSessionUseCase refreshSessionUseCase(TokenProviderPort tokenProviderPort,
                                                       UserRightsFetcher userRightsFetcher) {
        return new RefreshSessionUseCase(tokenProviderPort, userRightsFetcher);
    }
}
