package com.vetcare_back.mapper;

import com.vetcare_back.dto.user.*;
import com.vetcare_back.entity.User;
import org.mapstruct.*;

import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // UserRegister
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Hasheado en service
    @Mapping(target = "role", constant = "OWNER")
    @Mapping(target = "active", constant = "true")
    User toEntity(UserRegisterDTO dto);

    // UserUpdateDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UserUpdateDTO dto, @MappingTarget User entity);

    // UserResponseDTO
    UserResponseDTO toResponseDTO(User entity);

    // ChangeRoleDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "role", source = "newRole")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(ChangeRoleDTO dto, @MappingTarget User entity);

    // DeactivateUserDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", constant = "false")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void deactivateUser(DeactivateUserDTO dto, @MappingTarget User entity);

    //ActivateUserDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", constant = "true")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void activateUser(ActivateUserDTO dto, @MappingTarget User entity);
}