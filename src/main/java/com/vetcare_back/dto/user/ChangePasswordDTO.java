package com.vetcare_back.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotNull(message = "User ID required")
    private Long id;

    @NotBlank(message = "New password required")
    @Size(min = 6, max = 255, message = "New password must be between 6 and 255 characters")
    private String newPassword;
}
