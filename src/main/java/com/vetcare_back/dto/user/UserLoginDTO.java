package com.vetcare_back.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "Email required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password required")
    private String password;
}
