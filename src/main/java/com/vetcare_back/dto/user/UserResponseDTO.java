package com.vetcare_back.dto.user;

import com.vetcare_back.entity.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private Boolean active;
}