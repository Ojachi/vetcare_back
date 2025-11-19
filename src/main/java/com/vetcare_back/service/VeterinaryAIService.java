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
            return getGenericVeterinaryAdvice(originalQuestion);
        }
        
        // Asegurar que siempre termine con recomendación veterinaria
        String response = aiResponse.trim();
        if (!response.toLowerCase().contains("veterinari") && !response.toLowerCase().contains("consulta")) {
            response += "\n\n⚠️ Recomendamos consultar con un veterinario para una evaluación profesional.";
        }
        
        return response;
    }
    
    private String getGenericVeterinaryAdvice(String question) {
        String lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.contains("vomit") || lowerQuestion.contains("vómit")) {
            return """
                El vómito en mascotas puede tener varias causas:
                
                🔸 Causas comunes:
                - Comer muy rápido
                - Cambio brusco de dieta
                - Estrés o ansiedad
                
                🔸 Primeros auxilios:
                - Retira la comida por 2-4 horas
                - Ofrece agua en pequeñas cantidades
                - Mantén a la mascota en reposo
                
                ⚠️ Consulta veterinaria urgente si:
                - Vómito con sangre
                - Más de 3 episodios en 24h
                - Letargo o deshidratación
                """;
        }
        
        if (lowerQuestion.contains("diarrea")) {
            return """
                La diarrea puede indicar varios problemas:
                
                🔸 Cuidados inmediatos:
                - Dieta blanda (arroz cocido, pollo hervido)
                - Mantener hidratación
                - Observar frecuencia y consistencia
                
                ⚠️ Consulta veterinaria si:
                - Diarrea con sangre
                - Persiste más de 24 horas
                - Signos de deshidratación
                """;
        }
        
        return """
            Para cualquier síntoma en tu mascota:
            
            🔸 Observa y registra:
            - Duración de los síntomas
            - Frecuencia e intensidad
            - Cambios en comportamiento
            
            🔸 Mantén a tu mascota:
            - En un lugar cómodo y tranquilo
            - Con acceso a agua fresca
            - Bajo observación constante
            
            ⚠️ La evaluación veterinaria profesional es siempre recomendada para determinar la causa exacta y el tratamiento apropiado.
            """;
    }
    
    private String detectGreetingOrGeneral(String message) {
        String lower = message.toLowerCase().trim();
        
        // Saludos
        if (lower.matches("^(hola|hi|hello|hey|buenos días|buenas tardes|buenas noches|saludos)$")) {
            return "¡Hola! 🐾 Soy el asistente veterinario de VetCare. \n\n" +
                   "¿Cómo puedo ayudarte hoy con tu mascota? Puedes preguntarme sobre:\n" +
                   "• Síntomas o comportamientos extraños\n" +
                   "• Cuidados básicos\n" +
                   "• Primeros auxilios\n" +
                   "• Alimentación";
        }
        
        // Agradecimientos
        if (lower.matches("^(gracias|thanks|thank you|muchas gracias)$")) {
            return "¡De nada! 😊 Si tienes más preguntas sobre tu mascota, estoy aquí para ayudarte.";
        }
        
        // Despedidas
        if (lower.matches("^(adiós|adios|bye|chao|hasta luego)$")) {
            return "¡Hasta pronto! 🐾 Cuida bien de tu mascota.";
        }
        
        // Mensajes muy cortos sin contexto veterinario
        if (lower.length() < 10 && !containsVeterinaryKeywords(lower)) {
            return "Por favor, cuéntame más sobre tu mascota. ¿Qué síntomas tiene o qué te preocupa?";
        }
        
        return null;
    }
    
    private boolean containsVeterinaryKeywords(String message) {
        String[] keywords = {"perro", "gato", "mascota", "animal", "vómit", "diarrea", 
                            "fiebre", "enferm", "dolor", "comer", "beber", "orina", "heces"};
        for (String keyword : keywords) {
            if (message.contains(keyword)) return true;
        }
        return false;
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