package com.vetcare_back.mapper;

import com.vetcare_back.dto.pet.PetResponseDTO;
import com.vetcare_back.entity.Pet;
import com.vetcare_back.dto.pet.PetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "active", ignore = true)
    Pet toEntity(PetDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "species", target = "species")
    @Mapping(source = "breed", target = "breed")
    @Mapping(source = "age", target = "age")
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "sex", target = "sex")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "active", target = "active")
    PetResponseDTO toResponseDTO(Pet pet);

    void updateEntity(PetDTO dto, @MappingTarget Pet pet);
}
