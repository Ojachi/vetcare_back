package com.vetcare_back.mapper;

import com.vetcare_back.dto.diagnosis.DiagnosisDTO;
import com.vetcare_back.dto.diagnosis.DiagnosisResponseDTO;
import com.vetcare_back.entity.Diagnosis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {AppointmentMapper.class, UserMapper.class})
public interface DiagnosisMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "vet", ignore = true)
    @Mapping(target = "active", ignore = true)
    Diagnosis toEntity(DiagnosisDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "appointment", target = "appointment")
    @Mapping(source = "vet", target = "vet")  // Usa UserMapper
    @Mapping(source = "description", target = "description")
    @Mapping(source = "treatment", target = "treatment")
    @Mapping(source = "medications", target = "medications")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "active", target = "active")
    DiagnosisResponseDTO toResponseDTO(Diagnosis diagnosis);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "vet", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(DiagnosisDTO dto, @MappingTarget Diagnosis diagnosis);
}
