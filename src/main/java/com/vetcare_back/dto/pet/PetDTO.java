package com.vetcare_back.dto.pet;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PetDTO {
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "specie is required")
    private String species;

    @NotNull(message = "breed is required")
    private String breed;

    @Positive(message = "Age must be positive")
    private int age;

    @Positive(message = "Weight must be positive")
    private Double weight;

    @NotNull(message = "Sex is required")
    private String sex;

    private Long ownerId;
}
