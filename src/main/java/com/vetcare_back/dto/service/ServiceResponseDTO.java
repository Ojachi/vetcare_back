package com.vetcare_back.dto.service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean requiresVeterinarian;
    private Boolean active;
}
