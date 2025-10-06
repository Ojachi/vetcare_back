package com.vetcare_back.service;

import com.vetcare_back.dto.password.ChangePasswordAuthenticatedDTO;
import com.vetcare_back.dto.password.ForgotPasswordDTO;
import com.vetcare_back.dto.password.ResetPasswordDTO;
import com.vetcare_back.dto.password.VerifyOtpDTO;
import com.vetcare_back.entity.PasswordResetToken;
import com.vetcare_back.entity.User;
import com.vetcare_back.exception.UserNotFoundExeption;
import com.vetcare_back.repository.PasswordResetTokenRepository;
import com.vetcare_back.repository.UserRepository;
import com.vetcare_back.util.OtpUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PasswordResetServiceImpl implements IPasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${otp.expiration.minutes:10}")
    private int otpExpirationMinutes;

    @Value("${otp.max.attempts:3}")
    private int maxAttempts;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetTokenRepository tokenRepository,
                                    IEmailService emailService,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundExeption("User not found with email: " + dto.getEmail()));

        if (!user.getActive()) {
            throw new IllegalStateException("User account is deactivated");
        }

        // Invalidar tokens anteriores no usados
        List<PasswordResetToken> oldTokens = tokenRepository.findByUserAndUsedFalseAndVerifiedFalse(user);
        if (!oldTokens.isEmpty()) {
            tokenRepository.deleteAll(oldTokens);
        }

        // Generar nuevo OTP
        String otp = OtpUtil.generateOtp();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .otp(otp)
                .expiryDate(expiryDate)
                .verified(false)
                .used(false)
                .attempts(0)
                .build();

        tokenRepository.save(token);

        // Enviar email
        emailService.sendOtpEmail(user.getEmail(), otp, user.getName());
    }

    @Override
    @Transactional
    public void verifyOtp(VerifyOtpDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundExeption("User not found"));

        PasswordResetToken token = tokenRepository.findByUserAndOtpAndVerifiedFalseAndUsedFalse(user, dto.getOtp())
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        // Validar expiración
        if (token.isExpired()) {
            tokenRepository.delete(token);
            throw new IllegalStateException("OTP has expired");
        }

        // Validar intentos
        token.setAttempts(token.getAttempts() + 1);
        if (token.getAttempts() > maxAttempts) {
            tokenRepository.delete(token);
            throw new IllegalStateException("Maximum OTP verification attempts exceeded");
        }

        // Marcar como verificado
        token.setVerified(true);
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        // Validar que las contraseñas coincidan
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundExeption("User not found"));

        // Buscar token verificado y no usado
        PasswordResetToken token = tokenRepository
                .findTopByUserAndVerifiedTrueAndUsedFalseOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new IllegalStateException("No verified OTP found. Please verify OTP first"));

        // Validar que el OTP sea el correcto
        if (!token.getOtp().equals(dto.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        // Validar que no haya expirado
        if (token.isExpired()) {
            tokenRepository.delete(token);
            throw new IllegalStateException("OTP has expired");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Marcar token como usado
        token.setUsed(true);
        tokenRepository.save(token);

        // Enviar confirmación
        emailService.sendPasswordChangedConfirmation(user.getEmail(), user.getName());
    }

    @Override
    @Transactional
    public void changePasswordAuthenticated(ChangePasswordAuthenticatedDTO dto) {
        // Validar que las contraseñas coincidan
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Obtener usuario autenticado
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("User not found"));

        // Validar contraseña actual
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validar que la nueva contraseña sea diferente
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Enviar confirmación
        emailService.sendPasswordChangedConfirmation(user.getEmail(), user.getName());
    }

    @Override
    @Transactional
    public void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpiryDateBefore(now);
    }
}