package com.vetcare_back.dto.species;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreedResponseDTO {
    private Long id;
    private String name;
    private Long speciesId;
    private String speciesName;
    private Boolean active;
}
