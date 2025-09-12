package com.vetcare_back.dto.diagnosis;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DiagnosisDTO {
    @NotNull(message = "appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "description is required")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @Size(max = 1000, message = "Treatment must be less than 1000 characters")
    private String treatment;

    @Size(max = 1000, message = "Medications must be less than 1000 characters")
    private String medications;

    @NotNull(message = "Localdate is required")
    @PastOrPresent(message = "")
    private LocalDate date;

    private Boolean active;
}
