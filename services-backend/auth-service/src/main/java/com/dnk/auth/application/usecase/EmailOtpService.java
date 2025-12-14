package com.dnk.auth.application.usecase;

import com.dnk.auth.application.port.out.EmailSenderPort;

public class EmailOtpService {

    private final EmailSenderPort emailSenderPort;

    public EmailOtpService(EmailSenderPort emailSenderPort) {
        this.emailSenderPort = emailSenderPort;
    }

    public void sendOtp(String toEmail, String otpCode) {
        emailSenderPort.sendOtpEmail(toEmail, otpCode);
    }
}
