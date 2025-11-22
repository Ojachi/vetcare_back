package com.vetcare_back.dto.species;

import com.vetcare_back.entity.Species;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesResponseDTO {
    private Long id;
    private String name;
    private Boolean active;
    
    public static SpeciesResponseDTO fromEntity(Species species) {
        return SpeciesResponseDTO.builder()
                .id(species.getId())
                .name(species.getName())
                .active(species.getActive())
                .build();
    }
}
