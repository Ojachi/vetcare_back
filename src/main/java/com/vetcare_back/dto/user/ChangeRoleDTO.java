package com.vetcare_back.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleDTO {
    @NotNull(message = "User ID required")
    private Long id;

    @NotNull(message = "Role required")
    private String newRole;
}