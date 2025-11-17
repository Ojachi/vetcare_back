package com.vetcare_back.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatRateLimiter {
    
    private final Map<String, List<LocalDateTime>> requests = new ConcurrentHashMap<>();
    
    public boolean isAllowed(HttpServletRequest request) {
        String identifier = getClientIdentifier(request);
        List<LocalDateTime> userRequests = requests.computeIfAbsent(identifier, k -> new ArrayList<>());
        LocalDateTime now = LocalDateTime.now();
        
        // Limpiar requests antiguos (últimos 5 minutos)
        userRequests.removeIf(time -> time.isBefore(now.minusMinutes(5)));
        
        // Límites diferentes según usuario
        int maxRequests = isAuthenticatedUser(request) ? 20 : 5;
        
        if (userRequests.size() >= maxRequests) {
            return false;
        }
        
        userRequests.add(now);
        return true;
    }
    
    private String getClientIdentifier(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return auth.getName();
        }
        
        String ip = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        return ip + ":" + (userAgent != null ? userAgent.hashCode() : "unknown");
    }
    
    private boolean isAuthenticatedUser(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}