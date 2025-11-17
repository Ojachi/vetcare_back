package com.vetcare_back.controller.chat;

import com.vetcare_back.dto.chat.ChatRequestDTO;
import com.vetcare_back.dto.chat.ChatResponseDTO;
import com.vetcare_back.service.VeterinaryAIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@Validated
@Slf4j
public class ChatController {
    
    private final VeterinaryAIService veterinaryAIService;
    
    public ChatController(VeterinaryAIService veterinaryAIService) {
        this.veterinaryAIService = veterinaryAIService;
    }
    
    @PostMapping("/consult")
    public ResponseEntity<ChatResponseDTO> consultVeterinarian(
            @Valid @RequestBody ChatRequestDTO request,
            HttpServletRequest httpRequest) {
        
        String userIdentifier = getUserIdentifier();
        log.info("Chat consultation from: {}", userIdentifier);
        
        ChatResponseDTO response = veterinaryAIService.getVeterinaryAdvice(
            request.getMessage(), 
            httpRequest
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getServiceStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "active",
            "service", "Veterinary AI Assistant",
            "version", "1.0"
        ));
    }
    
    private String getUserIdentifier() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return auth.getName();
        }
        return "anonymous";
    }
}