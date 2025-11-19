package com.vetcare_back.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HuggingFaceService {
    
    @Value("${huggingface.api.key}")
    private String apiKey;
    
    @Value("${huggingface.base.url}")
    private String baseUrl;
    
    @Value("${huggingface.models}")
    private String[] models;
    
    private final RestTemplate restTemplate;
    private int currentModelIndex = 0;
    
    public HuggingFaceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public ResponseWithSource generateResponse(String prompt) {
        // Intentar con diferentes modelos si uno falla
        for (int attempt = 0; attempt < models.length; attempt++) {
            String model = models[currentModelIndex];
            String url = baseUrl + "/chat/completions";
            
            try {
                String response = callModel(url, model, prompt);
                if (response != null && !response.trim().isEmpty()) {
                    return new ResponseWithSource(response, "AI", model);
                }
            } catch (Exception e) {
                log.warn("Model {} failed: {}", model, e.getMessage());
            }
            
            // Rotar al siguiente modelo
            currentModelIndex = (currentModelIndex + 1) % models.length;
        }
        
        // Si todos los modelos fallan, usar respuesta offline
        return new ResponseWithSource(getOfflineResponse(prompt), "OFFLINE", null);
    }
    
    public static class ResponseWithSource {
        public final String response;
        public final String source;
        public final String model;
        
        public ResponseWithSource(String response, String source, String model) {
            this.response = response;
            this.source = source;
            this.model = model;
        }
    }
    
    private String callModel(String url, String model, String prompt) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "system", "content", "Eres un asistente veterinario. Responde SOLO con consejos prácticos inmediatos y SIEMPRE termina recomendando agendar cita en la veterinaria. Sé directo, no uses frases como 'Lo siento' o 'es importante recordar'. Máximo 100 palabras."),
                Map.of("role", "user", "content", prompt)
            ),
            "max_tokens", 150,
            "temperature", 0.7
        );
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        
        if (response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            
            if (body.containsKey("error")) {
                throw new RuntimeException("API Error: " + body.get("error"));
            }
            
            if (body.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    if (message != null && message.containsKey("content")) {
                        return (String) message.get("content");
                    }
                }
            }
        }
        
        return null;
    }
    
    private String getFallbackResponse() {
        return "Lo siento, el servicio de consulta no está disponible en este momento. " +
               "Para consultas urgentes, contacta directamente con la veterinaria.";
    }
    
    private String getOfflineResponse(String prompt) {
        String lowerPrompt = prompt.toLowerCase();
        
        if (lowerPrompt.contains("vomit") || lowerPrompt.contains("vómit")) {
            return "El vómito en mascotas puede ser causado por comer muy rápido, cambios en la dieta o estrés. " +
                   "Retira la comida por 2-4 horas y ofrece agua en pequeñas cantidades. " +
                   "Si persiste o hay sangre, consulta inmediatamente con un veterinario.";
        }
        
        if (lowerPrompt.contains("diarrea")) {
            return "La diarrea puede indicar problemas digestivos. Ofrece dieta blanda (arroz cocido, pollo hervido) " +
                   "y mantén la hidratación. Si hay sangre o persiste más de 24 horas, consulta con un veterinario.";
        }
        
        if (lowerPrompt.contains("fiebre") || lowerPrompt.contains("caliente")) {
            return "La fiebre en mascotas es seria. Mantén a tu mascota en un lugar fresco y ofrece agua. " +
                   "La fiebre siempre requiere atención veterinaria inmediata.";
        }
        
        return "Para cualquier síntoma en tu mascota, observa la duración e intensidad. " +
               "Mantén a tu mascota cómoda y con acceso a agua. " +
               "Siempre es recomendable consultar con un veterinario para un diagnóstico preciso.";
    }
}