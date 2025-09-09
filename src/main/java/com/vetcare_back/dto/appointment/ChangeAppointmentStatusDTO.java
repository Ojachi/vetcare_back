package com.vetcare_back.dto.appointment;

import com.vetcare_back.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeAppointmentStatusDTO {
    @NotNull(message = "Status is required")
    private AppointmentStatus status;
}
