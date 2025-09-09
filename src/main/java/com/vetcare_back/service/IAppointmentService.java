package com.vetcare_back.service;

import com.vetcare_back.dto.appointment.AppointmentDTO;
import com.vetcare_back.dto.appointment.AppointmentResponseDTO;
import com.vetcare_back.dto.appointment.ChangeAppointmentStatusDTO;
import com.vetcare_back.entity.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {
    AppointmentResponseDTO create(AppointmentDTO dto);
    AppointmentResponseDTO update(Long id, AppointmentDTO dto);
    void cancel(Long id);
    void changeStatus(Long id, ChangeAppointmentStatusDTO dto);
    AppointmentResponseDTO getById(Long id);
    List<AppointmentResponseDTO> listAll();
    List<AppointmentResponseDTO> listByFilters(Long ownerId, Long petId, Long serviceId, Long assignedToId, LocalDateTime startDate, LocalDateTime endDate);
}
