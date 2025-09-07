package com.vetcare_back.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeactivateUserDTO {
    @NotNull(message = "User ID required")
    private Long id;
}