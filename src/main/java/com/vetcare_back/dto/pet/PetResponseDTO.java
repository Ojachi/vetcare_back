package com.vetcare_back.dto.pet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vetcare_back.dto.user.UserResponseDTO;
import lombok.Data;

@Data
public class PetResponseDTO {
    private Long id;
    private String name;
    private Long speciesId;
    private String speciesName;
    private String customSpecies;
    private Long breedId;
    private String breedName;
    private String customBreed;
    private int age;
    private Double weight;
    private String sex;
    private UserResponseDTO owner;
    private Boolean active;
}
