package com.dnk.auth.infrastructure.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.dnk.auth.application.port.out.EmailSenderPort;

@Component
public class MailtrapEmailSender implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(MailtrapEmailSender.class);

    private static final String SUBJECT = "Your verification code";

    private final JavaMailSender mailSender;

    public MailtrapEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(SUBJECT);
        message.setText("Your verification code is: " + otpCode);
        mailSender.send(message);

        log.info("OTP email sent to {}", toEmail);
    }
}
