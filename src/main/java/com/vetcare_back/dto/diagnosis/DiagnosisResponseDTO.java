package com.vetcare_back.dto.diagnosis;

import com.vetcare_back.dto.appointment.AppointmentResponseDTO;
import com.vetcare_back.dto.user.UserResponseDTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DiagnosisResponseDTO {
    private Long id;
    private AppointmentResponseDTO appointment;
    private UserResponseDTO vet;
    private String description;
    private String treatment;
    private String medications;
    private LocalDate date;
    private Boolean active;
}
