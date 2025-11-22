package com.vetcare_back.dto.pet;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PetDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must be less than 120 characters")
    private String name;

    private Long speciesId;

    @Size(max = 100, message = "Custom species must be less than 100 characters")
    private String customSpecies;

    private Long breedId;

    @Size(max = 120, message = "Custom breed must be less than 120 characters")
    private String customBreed;

    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 100, message = "Age must be less than 100")
    private int age;

    @Positive(message = "Weight must be positive")
    @Max(value = 1000, message = "Weight must be less than 1000 kg")
    private Double weight;

    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^(Macho|Hembra)$", message = "Sex must be 'Macho' or 'Hembra'")
    private String sex;

    private Long ownerId;
}
