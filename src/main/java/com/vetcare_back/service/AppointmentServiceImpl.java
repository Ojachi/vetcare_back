package com.vetcare_back.service;

import com.vetcare_back.dto.appointment.AppointmentDTO;
import com.vetcare_back.dto.appointment.AppointmentResponseDTO;
import com.vetcare_back.dto.appointment.AvailableProfessionalDTO;
import com.vetcare_back.dto.appointment.ChangeAppointmentStatusDTO;
import com.vetcare_back.entity.*;
import com.vetcare_back.exception.UserNotFoundExeption;
import com.vetcare_back.mapper.AppointmentMapper;
import com.vetcare_back.mapper.UserMapper;
import com.vetcare_back.repository.AppointmentRepository;
import com.vetcare_back.repository.DiagnosisRepository;
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
    private final UserMapper userMapper;
    private final DiagnosisRepository diagnosisRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PetRepository petRepository,
                                  ServiceRepository serviceRepository, UserRepository userRepository,
                                  AppointmentMapper appointmentMapper, UserMapper userMapper,
                                  DiagnosisRepository diagnosisRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.appointmentMapper = appointmentMapper;
        this.userMapper = userMapper;
        this.diagnosisRepository = diagnosisRepository;
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

        if (!service.isActive()) {
            throw new IllegalStateException("Service is not active");
        }

        // Validar permisos para crear
        if (hasRole(currentUser,"OWNER") && !pet.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Owners can only create appointments for their own pets");
        } else if (!hasRole(currentUser, "OWNER") && !hasRole(currentUser, "EMPLOYEE") && !hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Unauthorized to create appointments");
        }

        // Asignación de profesional (manual o automática)
        User assignedTo;
        if (dto.getAssignedToId() != null) {
            assignedTo = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new UserNotFoundExeption("Assigned user not found"));
            validateProfessional(assignedTo, service, dto.getStartDateTime());
        } else {
            assignedTo = findAvailableProfessional(service, dto.getStartDateTime());
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

        User assignedTo;
        if (dto.getAssignedToId() != null) {
            assignedTo = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new UserNotFoundExeption("Assigned user not found"));
            validateProfessionalForUpdate(assignedTo, service, dto.getStartDateTime(), id);
        } else {
            assignedTo = appointment.getAssignedTo();
            // Validar disponibilidad si cambia la fecha
            if (!dto.getStartDateTime().equals(appointment.getStartDateTime())) {
                validateProfessionalForUpdate(assignedTo, service, dto.getStartDateTime(), id);
            }
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

        /* Solo el asignado, employee o admin puede cambiar estado */
        if (!currentUser.getId().equals(appointment.getAssignedTo().getId()) && !hasRole("ADMIN") && !hasRole("EMPLOYEE")) {
            throw new SecurityException("Only the assigned user, employee or admin can change appointment status");
        }

        /* Validar transición de estado */
        AppointmentStatus newStatus = dto.getStatus();
        if (!isValidStatusTransition(appointment.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + appointment.getStatus() + " to " + newStatus);
        }

        /* Validar que servicios con veterinario tengan diagnóstico antes de completar */
        if (newStatus == AppointmentStatus.COMPLETED && appointment.getService().getRequiresVeterinarian()) {
            if (diagnosisRepository.findByAppointment(appointment).isEmpty()) {
                throw new IllegalStateException("Cannot complete appointment: diagnosis is required for veterinary services");
            }
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
        Role userRole = currentUser.getRole();
        
        if (userRole == Role.ADMIN || userRole == Role.EMPLOYEE) {
            appointments = appointmentRepository.findAll();
        } else if (userRole == Role.VETERINARIAN) {
            appointments = appointmentRepository.findByAssignedTo(currentUser);
        } else if (userRole == Role.OWNER) {
            appointments = appointmentRepository.findByOwner(currentUser);
        } else {
            throw new SecurityException("Unauthorized to list appointments");
        }
        return appointments.stream().map(appointmentMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> listByFilters(Long ownerId, Long petId, Long serviceId, Long assignedToId, LocalDateTime startDate, LocalDateTime endDate) {
        if (!hasRole("ADMIN") && !hasRole("EMPLOYEE")) {
            throw new SecurityException("Only admins and employees can filter appointments");
        }

        List<Appointment> appointments = appointmentRepository.findAll();
        if (ownerId != null) {
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
        return hasRole("OWNER") && appointment.getOwner().getId().equals(currentUser.getId());
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
        return switch (current) {
            case PENDING -> next == AppointmentStatus.ACCEPTED || next == AppointmentStatus.CANCELLED;
            case ACCEPTED -> next == AppointmentStatus.COMPLETED || next == AppointmentStatus.CANCELLED;
            default -> false;
        };
    }

    @Override
    public List<AvailableProfessionalDTO> getAvailableProfessionals(Long serviceId, LocalDateTime dateTime) {
        Services service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        List<Role> allowedRoles = getAllowedRolesForService(service);
        List<User> professionals = userRepository.findByRoleInAndActiveTrue(allowedRoles);

        return professionals.stream()
                .map(prof -> {
                    boolean available = !hasOverlappingAppointment(prof, dateTime, service.getDurationMinutes(), null);
                    String nextSlot = available ? null : findNextAvailableSlot(prof, dateTime, service.getDurationMinutes());
                    return new AvailableProfessionalDTO(userMapper.toResponseDTO(prof), available, nextSlot);
                })
                .collect(Collectors.toList());
    }

    private void validateProfessional(User professional, Services service, LocalDateTime dateTime) {
        validateProfessionalRole(professional, service);
        
        if (hasOverlappingAppointment(professional, dateTime, service.getDurationMinutes(), null)) {
            throw new IllegalStateException("Professional is not available at this time");
        }
    }

    private void validateProfessionalForUpdate(User professional, Services service, LocalDateTime dateTime, Long appointmentId) {
        validateProfessionalRole(professional, service);
        
        if (hasOverlappingAppointment(professional, dateTime, service.getDurationMinutes(), appointmentId)) {
            throw new IllegalStateException("Professional is not available at this time");
        }
    }

    private User findAvailableProfessional(Services service, LocalDateTime dateTime) {
        List<Role> allowedRoles = getAllowedRolesForService(service);
        List<User> professionals = userRepository.findByRoleInAndActiveTrue(allowedRoles);

        return professionals.stream()
                .filter(prof -> !hasOverlappingAppointment(prof, dateTime, service.getDurationMinutes(), null))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No professionals available at this time"));
    }

    private String findNextAvailableSlot(User professional, LocalDateTime fromDateTime, Integer durationMinutes) {
        LocalDateTime checkTime = fromDateTime.plusMinutes(30);
        for (int i = 0; i < 20; i++) {
            if (!hasOverlappingAppointment(professional, checkTime, durationMinutes, null)) {
                return checkTime.toString();
            }
            checkTime = checkTime.plusMinutes(30);
        }
        return null;
    }

    private List<Role> getAllowedRolesForService(Services service) {
        return service.getRequiresVeterinarian() 
                ? List.of(Role.VETERINARIAN) 
                : List.of(Role.VETERINARIAN, Role.EMPLOYEE);
    }

    private void validateProfessionalRole(User professional, Services service) {
        if (!professional.getActive()) {
            throw new IllegalArgumentException("Professional is not active");
        }

        if (!hasRole(professional, "EMPLOYEE") && !hasRole(professional, "VETERINARIAN")) {
            throw new IllegalArgumentException("Assigned user must have EMPLOYEE or VETERINARIAN role");
        }

        if (service.getRequiresVeterinarian() && !hasRole(professional, "VETERINARIAN")) {
            throw new IllegalArgumentException("This service requires a veterinarian");
        }
    }

    private boolean hasOverlappingAppointment(User professional, LocalDateTime startDateTime, Integer durationMinutes, Long excludeAppointmentId) {
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);
        
        List<Appointment> overlappingAppointments = appointmentRepository
                .findByAssignedToAndStartDateTimeBetweenAndStatusNot(
                        professional, 
                        startDateTime.minusMinutes(120), // Buffer para citas que puedan solaparse
                        endDateTime, 
                        AppointmentStatus.CANCELLED
                );

        return overlappingAppointments.stream()
                .filter(apt -> excludeAppointmentId == null || !apt.getId().equals(excludeAppointmentId))
                .anyMatch(apt -> {
                    LocalDateTime existingEnd = apt.getStartDateTime().plusMinutes(apt.getService().getDurationMinutes());
                    // Hay solapamiento si: nuevaStart < existingEnd AND nuevaEnd > existingStart
                    return startDateTime.isBefore(existingEnd) && endDateTime.isAfter(apt.getStartDateTime());
                });
    }
}