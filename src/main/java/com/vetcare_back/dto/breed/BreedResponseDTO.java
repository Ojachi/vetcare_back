package com.vetcare_back.dto.breed;

import com.vetcare_back.entity.Breed;
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

    public static BreedResponseDTO fromEntity(Breed breed) {
        return BreedResponseDTO.builder()
                .id(breed.getId())
                .name(breed.getName())
                .speciesId(breed.getSpecies().getId())
                .speciesName(breed.getSpecies().getName())
                .active(breed.getActive())
                .build();
    }
}
