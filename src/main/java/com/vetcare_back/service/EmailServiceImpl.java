package com.vetcare_back.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements IEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    @Override
    public void sendOtpEmail(String to, String otp, String userName) {
        try {
            Email from = new Email(fromEmail, fromName);
            Email toEmail = new Email(to);
            String subject = "VetCare - Código de Recuperación de Contraseña";
            Content content = new Content("text/plain", buildOtpEmailBody(otp, userName));

            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                System.err.println("SendGrid error: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
                throw new RuntimeException("Failed to send email via SendGrid");
            }

            System.out.println("OTP email sent successfully to: " + to);

        } catch (IOException e) {
            System.err.println("Error sending OTP email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        }
    }

    @Override
    public void sendPasswordChangedConfirmation(String to, String userName) {
        try {
            Email from = new Email(fromEmail, fromName);
            Email toEmail = new Email(to);
            String subject = "VetCare - Contraseña Cambiada Exitosamente";
            Content content = new Content("text/plain", buildPasswordChangedBody(userName));

            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                System.err.println("SendGrid error: " + response.getStatusCode());
                // No lanzamos excepción porque la contraseña ya se cambió
            } else {
                System.out.println("Confirmation email sent successfully to: " + to);
            }

        } catch (IOException e) {
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