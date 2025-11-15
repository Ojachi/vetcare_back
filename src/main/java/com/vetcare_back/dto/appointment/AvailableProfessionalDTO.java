package com.vetcare_back.dto.appointment;

import com.vetcare_back.dto.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableProfessionalDTO {
    private UserResponseDTO professional;
    private boolean available;
    private String nextAvailableSlot;
}
