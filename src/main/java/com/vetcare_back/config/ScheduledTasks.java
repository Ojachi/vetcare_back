package com.vetcare_back.config;

import com.vetcare_back.service.IPasswordResetService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private final IPasswordResetService passwordResetService;

    public ScheduledTasks(IPasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Limpia tokens expirados cada día a las 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredTokens() {
        passwordResetService.cleanExpiredTokens();
        System.out.println("Expired password reset tokens cleaned at " + java.time.LocalDateTime.now());
    }
}