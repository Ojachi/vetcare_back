package com.vetcare_back.service;

import com.vetcare_back.dto.appointment.AppointmentDTO;
import com.vetcare_back.dto.appointment.AppointmentResponseDTO;
import com.vetcare_back.dto.appointment.ChangeAppointmentStatusDTO;
import com.vetcare_back.entity.Appointment;
import com.vetcare_back.entity.AppointmentStatus;
import com.vetcare_back.entity.Pet;
import com.vetcare_back.entity.Services;
import com.vetcare_back.entity.User;
import com.vetcare_back.exception.UserNotFoundExeption;
import com.vetcare_back.mapper.AppointmentMapper;
import com.vetcare_back.repository.AppointmentRepository;
import com.vetcare_back.repository.PetRepository;
import com.vetcare_back.repository.ServiceRepository;
import com.vetcare_back.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PetRepository petRepository,
                                  ServiceRepository serviceRepository, UserRepository userRepository,
                                  AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public AppointmentResponseDTO create(AppointmentDTO dto) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        Services service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        User assignedTo = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new UserNotFoundExeption("Assigned user not found"));

        // Validar que assignedTo sea empleado o veterinario
        if (!hasRole(assignedTo, "EMPLOYEE") && !hasRole(assignedTo, "VETERINARIAN")) {
            throw new IllegalArgumentException("Assigned user must have EMPLOYEE or VETERINARIAN role");
        }

        // Validar permisos para crear
        if (hasRole("USER") && !pet.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Users can only create appointments for their own pets");
        } else if (!hasRole("EMPLOYEE") && !hasRole("ADMIN")) {
            throw new SecurityException("Unauthorized to create appointments");
        }

        Appointment appointment = appointmentMapper.toEntity(dto);
        appointment.setPet(pet);
        appointment.setService(service);
        appointment.setOwner(pet.getOwner());
        appointment.setScheduledBy(currentUser);
        appointment.setAssignedTo(assignedTo);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setCreateAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(appointment);
    }

    @Override
    public AppointmentResponseDTO update(Long id, AppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (!canAccessAppointment(appointment, false)) {
            throw new SecurityException("Unauthorized to update this appointment");
        }

        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        Services service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        User assignedTo = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new UserNotFoundExeption("Assigned user not found"));

        if (!hasRole(assignedTo, "EMPLOYEE") && !hasRole(assignedTo, "VETERINARIAN")) {
            throw new IllegalArgumentException("Assigned user must have EMPLOYEE or VETERINARIAN role");
        }

        appointmentMapper.updateEntity(dto, appointment);
        appointment.setPet(pet);
        appointment.setService(service);
        appointment.setOwner(pet.getOwner());
        appointment.setAssignedTo(assignedTo);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(appointment);
    }

    @Override
    public void cancel(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (!canAccessAppointment(appointment, true)) {
            throw new SecurityException("Unauthorized to cancel this appointment");
        }
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Only PENDING appointments can be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void changeStatus(Long id, ChangeAppointmentStatusDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        /* Solo el asignado o admin puede cambiar estado */
        if (!currentUser.getId().equals(appointment.getAssignedTo().getId()) && !hasRole("ADMIN")) {
            throw new SecurityException("Only the assigned user or admin can change appointment status");
        }

        /* Validar transición de estado */
        AppointmentStatus newStatus = dto.getStatus();
        if (!isValidStatusTransition(appointment.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + appointment.getStatus() + " to " + newStatus);
        }

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
    }

    @Override
    public AppointmentResponseDTO getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (!canAccessAppointment(appointment, true)) {
            throw new SecurityException("Unauthorized to view this appointment");
        }
        return appointmentMapper.toResponseDTO(appointment);
    }

    @Override
    public List<AppointmentResponseDTO> listAll() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        List<Appointment> appointments;
        if (hasRole("USER")) {
            appointments = appointmentRepository.findByOwner(currentUser);
        } else if (hasRole("EMPLOYEE") || hasRole("VETERINARIAN")) {
            appointments = appointmentRepository.findByAssignedTo(currentUser);
        } else if (hasRole("ADMIN")) {
            appointments = appointmentRepository.findAll();
        } else {
            throw new SecurityException("Unauthorized to list appointments");
        }
        return appointments.stream().map(appointmentMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> listByFilters(Long ownerId, Long petId, Long serviceId, Long assignedToId, LocalDateTime startDate, LocalDateTime endDate) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can filter appointments");
        }

        List<Appointment> appointments = appointmentRepository.findAll();
        if (ownerId != null) {
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new UserNotFoundExeption("Owner not found"));
            appointments = appointments.stream()
                    .filter(a -> a.getOwner().getId().equals(ownerId))
                    .collect(Collectors.toList());
        }
        if (petId != null) {
            appointments = appointments.stream()
                    .filter(a -> a.getPet().getId().equals(petId))
                    .collect(Collectors.toList());
        }
        if (serviceId != null) {
            appointments = appointments.stream()
                    .filter(a -> a.getService().getId().equals(serviceId))
                    .collect(Collectors.toList());
        }
        if (assignedToId != null) {
            appointments = appointments.stream()
                    .filter(a -> a.getAssignedTo().getId().equals(assignedToId))
                    .collect(Collectors.toList());
        }
        if (startDate != null && endDate != null) {
            appointments = appointmentRepository.findByStartDateTimeBetween(startDate, endDate);
        }
        return appointments.stream().map(appointmentMapper::toResponseDTO).collect(Collectors.toList());
    }

    private boolean canAccessAppointment(Appointment appointment, boolean allowAssigned) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        if (hasRole("EMPLOYEE") || hasRole("ADMIN")) {
            return true;
        }
        if (allowAssigned && hasRole("VETERINARIAN") && appointment.getAssignedTo().getId().equals(currentUser.getId())) {
            return true;
        }
        return appointment.getOwner().getId().equals(currentUser.getId());
    }

    private boolean hasRole(User user, String roleName) {
        // Chequea directamente el enum Role del User (sin "ROLE_" prefix)
        // Asumiendo Role es enum con valores como EMPLOYEE, VETERINARIAN
        return user.getRole() != null && user.getRole().name().equals(roleName.toUpperCase());
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

    private boolean isValidStatusTransition(AppointmentStatus current, AppointmentStatus next) {
        switch (current) {
            case PENDING:
                return next == AppointmentStatus.ACCEPTED || next == AppointmentStatus.CANCELLED;
            case ACCEPTED:
                return next == AppointmentStatus.COMPLETED || next == AppointmentStatus.CANCELLED;
            default:
                return false;
        }
    }
}