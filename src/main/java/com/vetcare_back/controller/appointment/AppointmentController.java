package com.vetcare_back.controller.appointment;

import com.vetcare_back.dto.appointment.AppointmentDTO;
import com.vetcare_back.dto.appointment.AppointmentResponseDTO;
import com.vetcare_back.dto.appointment.ChangeAppointmentStatusDTO;
import com.vetcare_back.service.IAppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final IAppointmentService appointmentService;

    public AppointmentController(IAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create(@Valid @RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.update(id, dto));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeAppointmentStatusDTO dto) {
        appointmentService.changeStatus(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> listAll() {
        return ResponseEntity.ok(appointmentService.listAll());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<AppointmentResponseDTO>> listByFilters(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
        return ResponseEntity.ok(appointmentService.listByFilters(ownerId, petId, serviceId, assignedToId, start, end));
    }

    @GetMapping("/available-professionals")
    public ResponseEntity<List<com.vetcare_back.dto.appointment.AvailableProfessionalDTO>> getAvailableProfessionals(
            @RequestParam Long serviceId,
            @RequestParam String dateTime) {
        LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
        return ResponseEntity.ok(appointmentService.getAvailableProfessionals(serviceId, parsedDateTime));
    }
}