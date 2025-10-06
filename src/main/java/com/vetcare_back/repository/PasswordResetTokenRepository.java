package com.vetcare_back.repository;

import com.vetcare_back.entity.PasswordResetToken;
import com.vetcare_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByUserAndOtpAndVerifiedFalseAndUsedFalse(User user, String otp);

    Optional<PasswordResetToken> findTopByUserAndVerifiedTrueAndUsedFalseOrderByCreatedAtDesc(User user);

    List<PasswordResetToken> findByUserAndUsedFalseAndVerifiedFalse(User user);

    @Modifying
    void deleteByExpiryDateBefore(LocalDateTime date);
}