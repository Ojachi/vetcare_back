package com.vetcare_back.dto.pet;

import com.vetcare_back.dto.user.UserResponseDTO;

public class PetResponseDTO {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private int age;
    private Double weight;
    private String sex;
    private UserResponseDTO user;
    private Boolean active;
}
