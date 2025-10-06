package com.vetcare_back.service;

public interface IEmailService {

    /**
     * Envía un OTP por email
     * @param to Email destino
     * @param otp Código OTP
     * @param userName Nombre del usuario
     */
    void sendOtpEmail(String to, String otp, String userName);

    /**
     * Envía confirmación de cambio de contraseña
     * @param to Email destino
     * @param userName Nombre del usuario
     */
    void sendPasswordChangedConfirmation(String to, String userName);
}