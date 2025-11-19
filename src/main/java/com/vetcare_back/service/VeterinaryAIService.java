package com.vetcare_back.service;

import com.vetcare_back.dto.chat.ChatResponseDTO;
import com.vetcare_back.exception.TooManyRequestsException;
import com.vetcare_back.util.ChatRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class VeterinaryAIService {
    
    private final HuggingFaceService huggingFaceService;
    private final ChatRateLimiter rateLimiter;
    
    public VeterinaryAIService(HuggingFaceService huggingFaceService, ChatRateLimiter rateLimiter) {
        this.huggingFaceService = huggingFaceService;
        this.rateLimiter = rateLimiter;
    }
    
    public ChatResponseDTO getVeterinaryAdvice(String userMessage, HttpServletRequest request) {
        // Rate limiting
        if (!rateLimiter.isAllowed(request)) {
            throw new TooManyRequestsException("Rate limit exceeded. Try again in 5 minutes.");
        }
        
        // Validación de entrada
        validateInput(userMessage);
        
        // Detectar saludos y mensajes generales
        String greetingResponse = detectGreetingOrGeneral(userMessage);
        if (greetingResponse != null) {
            return ChatResponseDTO.builder()
                .response(greetingResponse)
                .timestamp(LocalDateTime.now())
                .source("OFFLINE")
                .build();
        }
        
        // Crear prompt veterinario especializado
        String veterinaryPrompt = buildVeterinaryPrompt(userMessage);
        
        try {
            HuggingFaceService.ResponseWithSource aiResponse = huggingFaceService.generateResponse(veterinaryPrompt);
            String formattedResponse = formatVeterinaryResponse(aiResponse.response, userMessage);
            
            return ChatResponseDTO.builder()
                .response(formattedResponse)
                .timestamp(LocalDateTime.now())
                .source(aiResponse.source)
                .build();
                
        } catch (Exception e) {
            log.error("Error generating veterinary advice", e);
            return getFallbackResponse();
        }
    }
    
    private String buildVeterinaryPrompt(String userMessage) {
        return String.format("""
            Consulta veterinaria: %s
            
            Responde con:
            1. Consejos inmediatos (2-3 puntos)
            2. Cuándo es urgente
            3. "Agenda una cita en nuestra veterinaria para evaluación completa"
            
            Máximo 80 palabras. Sé directo.""", userMessage);
    }
    
    private String formatVeterinaryResponse(String aiResponse, String originalQuestion) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            throw new RuntimeException("No response from AI service");
        }
        
        // Asegurar que siempre termine con recomendación veterinaria
        String response = aiResponse.trim();
        if (!response.toLowerCase().contains("veterinari") && !response.toLowerCase().contains("consulta")) {
            response += "\n\n⚠️ Recomendamos consultar con un veterinario para una evaluación profesional.";
        }
        
        return response;
    }
    
    private String getGenericVeterinaryAdvice(String question) {
        return null;
    }
    
    private String detectGreetingOrGeneral(String message) {
        String lower = message.toLowerCase().trim();
        
        // Solo saludos de 1 palabra
        if (lower.matches("^(hola|hi|hello|hey)$")) {
            return "¡Hola! 🐾 Soy el asistente veterinario de VetCare. ¿Cómo puedo ayudarte con tu mascota?";
        }
        
        // Solo agradecimientos de 1 palabra
        if (lower.matches("^(gracias|thanks)$")) {
            return "¡De nada! 😊";
        }
        
        // Todo lo demás va a la IA
        return null;
    }
    

    private void validateInput(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (message.length() > 500) {
            throw new IllegalArgumentException("Message too long. Maximum 500 characters.");
        }
    }
    
    private ChatResponseDTO getFallbackResponse() {
        return ChatResponseDTO.builder()
            .response("Lo siento, el servicio no está disponible en este momento. " +
                     "Para consultas urgentes, contacta directamente con la veterinaria.")
            .timestamp(LocalDateTime.now())
            .source("OFFLINE")
            .build();
    }
}