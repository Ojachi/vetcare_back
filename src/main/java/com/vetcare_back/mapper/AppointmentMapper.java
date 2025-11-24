package com.vetcare_back.mapper;

import com.vetcare_back.dto.appointment.AppointmentDTO;
import com.vetcare_back.dto.appointment.AppointmentResponseDTO;
import com.vetcare_back.entity.Appointment;
import com.vetcare_back.mapper.PetMapper;
import com.vetcare_back.mapper.ServiceMapper;
import com.vetcare_back.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PetMapper.class, ServiceMapper.class, UserMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "scheduledBy", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    Appointment toEntity(AppointmentDTO dto);

    @Mapping(source = "scheduledBy", target = "scheduleBy")
    AppointmentResponseDTO toResponseDTO(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "scheduledBy", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    void updateEntity(AppointmentDTO dto, @MappingTarget Appointment appointment);
}