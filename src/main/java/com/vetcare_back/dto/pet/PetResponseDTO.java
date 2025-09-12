package com.vetcare_back.dto.pet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vetcare_back.dto.user.UserResponseDTO;
import lombok.Data;

@Data
public class PetResponseDTO {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private int age;
    private Double weight;
    private String sex;
    private UserResponseDTO owner;
    private Boolean active;
}
