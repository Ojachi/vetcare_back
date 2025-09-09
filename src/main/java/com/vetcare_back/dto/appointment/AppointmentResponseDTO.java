package com.vetcare_back.dto.appointment;

import com.vetcare_back.dto.pet.PetResponseDTO;
import com.vetcare_back.dto.service.ServiceResponseDTO;
import com.vetcare_back.dto.user.UserResponseDTO;
import com.vetcare_back.entity.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponseDTO {
    private Long id;
    private PetResponseDTO pet;
    private ServiceResponseDTO service;
    private UserResponseDTO owner;
    private UserResponseDTO scheduleBy;
    private UserResponseDTO assignedTo;
    private LocalDateTime startDateTime;
    private AppointmentStatus status;
    private String note;
    private LocalDateTime createAt;
}
