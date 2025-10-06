package com.vetcare_back.controller.password;

import com.vetcare_back.dto.password.ChangePasswordAuthenticatedDTO;
import com.vetcare_back.dto.password.ForgotPasswordDTO;
import com.vetcare_back.dto.password.ResetPasswordDTO;
import com.vetcare_back.dto.password.VerifyOtpDTO;
import com.vetcare_back.service.IPasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    private final IPasswordResetService passwordResetService;

    public PasswordResetController(IPasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Paso 1: Usuario sin sesión - Solicita recuperación de contraseña
     * Genera OTP y lo envía por email
     */
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        passwordResetService.forgotPassword(dto);
        return ResponseEntity.ok("OTP sent to your email successfully");
    }

    /**
     * Paso 2: Usuario sin sesión - Verifica el OTP recibido
     */
    @PostMapping("/auth/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpDTO dto) {
        passwordResetService.verifyOtp(dto);
        return ResponseEntity.ok("OTP verified successfully");
    }

    /**
     * Paso 3: Usuario sin sesión - Resetea la contraseña con OTP verificado
     */
    @PostMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.ok("Password reset successfully");
    }

    /**
     * Usuario autenticado - Cambia su contraseña desde su perfil
     */
    @PutMapping("/users/change-password")
    public ResponseEntity<String> changePasswordAuthenticated(
            @Valid @RequestBody ChangePasswordAuthenticatedDTO dto) {
        passwordResetService.changePasswordAuthenticated(dto);
        return ResponseEntity.ok("Password changed successfully");
    }
}