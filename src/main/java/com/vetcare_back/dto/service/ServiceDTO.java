package com.vetcare_back.dto.service;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceDTO {

    private Long id;

    @NotNull(message = "Name is required")
    @Size(max = 120, message = "Name cannot exceed 120 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price canot be null")
    @PositiveOrZero(message = "Price must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Price must be have up 10 integer digits and 2 decimal placess")
    private BigDecimal price;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @NotNull(message = "Requieres veterinarian is required")
    private Boolean requiresVeterinarian;

    private Boolean active;

}
