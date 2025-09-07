package com.vetcare_back.mapper;

import com.vetcare_back.dto.service.ServiceDTO;
import com.vetcare_back.dto.service.ServiceResponseDTO;
import com.vetcare_back.entity.Services;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceMapper {

    ServiceResponseDTO toResponseDTO(Services service);

    Services toEntity(ServiceDTO dto);

    void updateEntity(ServiceDTO dto, @MappingTarget Services service);
}