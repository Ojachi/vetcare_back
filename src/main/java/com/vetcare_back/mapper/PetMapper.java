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
    @Mapping(target = "species", ignore = true)
    @Mapping(target = "breed", ignore = true)
    Pet toEntity(PetDTO dto);

    @Mapping(source = "species.id", target = "speciesId")
    @Mapping(source = "species.name", target = "speciesName")
    @Mapping(source = "breed.id", target = "breedId")
    @Mapping(source = "breed.name", target = "breedName")
    PetResponseDTO toResponseDTO(Pet pet);

    @Mapping(target = "species", ignore = true)
    @Mapping(target = "breed", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(PetDTO dto, @MappingTarget Pet pet);
}
