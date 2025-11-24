package com.vetcare_back.config;

import com.vetcare_back.entity.User;
import com.vetcare_back.entity.Role;
import com.vetcare_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String adminEmail = "admin@vetcare.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .name("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .phone("1234567890")
                    .address("VetCare Office")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created: " + adminEmail);
        } else {
            System.out.println("Admin user already exists: " + adminEmail);
        }
        String consumidorEmail = "consumidor.final@sistema.local";
        if (userRepository.findByEmail(consumidorEmail).isEmpty()) {
            User consumidorFinal = User.builder()
                    .name("Consumidor Final")
                    .email(consumidorEmail)
                    .password(passwordEncoder.encode("NoAccesible222222222222"))
                    .phone("222222222222")
                    .address("N/A")
                    .role(Role.OWNER)
                    .active(false)
                    .build();
            userRepository.save(consumidorFinal);
            System.out.println("✅ Consumidor Final (222222222222) created");
        } else {
            System.out.println("ℹ️  Consumidor Final already exists");
        }
    }
}