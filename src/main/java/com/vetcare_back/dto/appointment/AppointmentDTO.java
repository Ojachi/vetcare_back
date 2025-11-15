package com.vetcare_back.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private Long assignedToId; // Optional: if null, system will auto-assign

    @NotNull(message = "Start date and time is required")
    @Future(message = "Start date and time must be in the futuer")
    private LocalDateTime startDateTime;

    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;
}
