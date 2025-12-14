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

@Configuration
public class UseCaseConfig {

    @Bean
    public EmailOtpService emailOtpService(EmailSenderPort emailSenderPort) {
        return new EmailOtpService(emailSenderPort);
    }

    @Bean
    public GoogleLoginUseCase googleLoginUseCase(IdentityProviderPort identityProviderPort,
                                                 MfaOtpRepositoryPort mfaOtpRepositoryPort,
                                                 EmailOtpService emailOtpService) {
        return new GoogleLoginUseCase(identityProviderPort, mfaOtpRepositoryPort, emailOtpService);
    }

    @Bean
    public VerifyMfaOtpUseCase verifyMfaOtpUseCase(MfaOtpRepositoryPort mfaOtpRepositoryPort,
                                                   TokenProviderPort tokenProviderPort,
                                                   PasswordHashingPort passwordHashingPort) {
        return new VerifyMfaOtpUseCase(mfaOtpRepositoryPort, tokenProviderPort, passwordHashingPort);
    }

    @Bean
    public JwtService jwtService(TokenProviderPort tokenProviderPort) {
        return new JwtService(tokenProviderPort);
    }
}
