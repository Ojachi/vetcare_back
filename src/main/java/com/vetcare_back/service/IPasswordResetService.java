package com.vetcare_back.service;

import com.vetcare_back.dto.password.ForgotPasswordDTO;
import com.vetcare_back.dto.password.VerifyOtpDTO;
import com.vetcare_back.dto.password.ResetPasswordDTO;
import com.vetcare_back.dto.password.ChangePasswordAuthenticatedDTO;

public interface IPasswordResetService {

    /**
     * Genera OTP y lo envía por email (usuario sin sesión)
     */
    void forgotPassword(ForgotPasswordDTO dto);

    /**
     * Verifica el OTP ingresado
     */
    void verifyOtp(VerifyOtpDTO dto);

    /**
     * Resetea la contraseña con OTP verificado (usuario sin sesión)
     */
    void resetPassword(ResetPasswordDTO dto);

    /**
     * Cambia la contraseña para usuario autenticado
     */
    void changePasswordAuthenticated(ChangePasswordAuthenticatedDTO dto);

    /**
     * Limpia tokens expirados (tarea programada)
     */
    void cleanExpiredTokens();
}