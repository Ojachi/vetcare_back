package com.vetcare_back.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    
    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;
}