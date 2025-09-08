package com.vetcare_back.mapper;

import com.vetcare_back.dto.pet.PetResponseDTO;
import com.vetcare_back.entity.Pet;
import com.vetcare_back.dto.pet.PetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "active", ignore = true)
    Pet toEntity(PetDTO dto);

    PetResponseDTO toResponseDTO(Pet pet);

    void updateEntity(PetDTO dto, @MappingTarget Pet pet);
}
