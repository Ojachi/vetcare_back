package com.vetcare_back.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@vetcare.com}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String to, String otp, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("VetCare - Código de Recuperación de Contraseña");
            message.setText(buildOtpEmailBody(otp, userName));

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending OTP email: " + e.getMessage());
            throw new RuntimeException("Failed to send email. Please try again later.");
        }
    }

    @Override
    public void sendPasswordChangedConfirmation(String to, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("VetCare - Contraseña Cambiada Exitosamente");
            message.setText(buildPasswordChangedBody(userName));

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending confirmation email: " + e.getMessage());
            // No lanzamos excepción aquí porque la contraseña ya se cambió
        }
    }

    private String buildOtpEmailBody(String otp, String userName) {
        return String.format("""
            Hola %s,
            
            Has solicitado recuperar tu contraseña en VetCare.
            
            Tu código de verificación es: %s
            
            Este código es válido por 10 minutos.
            
            Si no solicitaste este cambio, ignora este correo.
            
            Saludos,
            Equipo VetCare
            """, userName, otp);
    }

    private String buildPasswordChangedBody(String userName) {
        return String.format("""
            Hola %s,
            
            Tu contraseña ha sido cambiada exitosamente.
            
            Si no realizaste este cambio, contacta inmediatamente al soporte.
            
            Saludos,
            Equipo VetCare
            """, userName);
    }
}