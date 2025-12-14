package com.dnk.auth.application.port.out;

public interface EmailSenderPort {

    void sendOtpEmail(String toEmail, String otpCode);
}
