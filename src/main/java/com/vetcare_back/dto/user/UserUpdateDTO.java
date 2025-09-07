package com.vetcare_back.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Size(max = 255)
    private String name;

    @Size(max = 40)
    private String phone;

    @Size(max = 255)
    private String address;
}