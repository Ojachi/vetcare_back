package com.vetcare_back.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "Name required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Email required")
    @Email(message = "Invalid email")
    @Size(max = 180)
    private String email;

    @NotBlank(message = "Password required")
    @Size(min = 6, max = 255)
    private String password;

    @Size(max = 40)
    private String phone;

    @Size(max = 255)
    private String address;
}
