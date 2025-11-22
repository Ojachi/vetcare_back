package com.vetcare_back.dto.species;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesDTO {

    @NotBlank(message = "Species name is required")
    @Size(max = 100, message = "Species name must not exceed 100 characters")
    private String name;
}
