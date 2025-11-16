package com.vetcare_back.service;

import com.vetcare_back.dto.diagnosis.DiagnosisDTO;
import com.vetcare_back.dto.diagnosis.DiagnosisResponseDTO;
import com.vetcare_back.entity.Appointment;
import com.vetcare_back.entity.AppointmentStatus;
import com.vetcare_back.entity.Diagnosis;
import com.vetcare_back.entity.Pet;
import com.vetcare_back.entity.User;
import com.vetcare_back.exception.UserNotFoundExeption;
import com.vetcare_back.mapper.DiagnosisMapper;
import com.vetcare_back.repository.AppointmentRepository;
import com.vetcare_back.repository.DiagnosisRepository;
import com.vetcare_back.repository.PetRepository;
import com.vetcare_back.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiagnosisServiceImpl implements IDiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DiagnosisMapper diagnosisMapper;
    private final PetRepository petRepository;
    private final IPdfService pdfService;

    public DiagnosisServiceImpl(DiagnosisRepository diagnosisRepository, AppointmentRepository appointmentRepository,
                                UserRepository userRepository, DiagnosisMapper diagnosisMapper, PetRepository petRepository,
                                IPdfService pdfService) {
        this.diagnosisRepository = diagnosisRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.diagnosisMapper = diagnosisMapper;
        this.petRepository = petRepository;
        this.pdfService = pdfService;
    }

    @Override
    public DiagnosisResponseDTO create(DiagnosisDTO dto) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        /* Solo vet o admin pueden crear */
        if (!hasRole(currentUser, "VETERINARIAN") && !hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only veterinarians or admins can create diagnoses");
        }

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Diagnosis can only be created for COMPLETED appointments");
        }
        if (diagnosisRepository.findByAppointment(appointment).isPresent()) {
            throw new IllegalStateException("Appointment already has a diagnosis");
        }

        Diagnosis diagnosis = diagnosisMapper.toEntity(dto);
        diagnosis.setAppointment(appointment);
        diagnosis.setVet(currentUser);  // Vet -> usuario autenticado
        diagnosis.setActive(true);
        diagnosis = diagnosisRepository.save(diagnosis);
        return diagnosisMapper.toResponseDTO(diagnosis);
    }

    @Override
    public DiagnosisResponseDTO update(Long id, DiagnosisDTO dto) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        /* Solo el vet que creó o el admin lo pueden actualizar */
        if (!currentUser.getId().equals(diagnosis.getVet().getId()) && !hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only the veterinarian who created or admin can update this diagnosis");
        }

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Diagnosis can only be updated for COMPLETED appointments");
        }

        diagnosisMapper.updateEntity(dto, diagnosis);
        diagnosis.setAppointment(appointment);
        diagnosis = diagnosisRepository.save(diagnosis);
        return diagnosisMapper.toResponseDTO(diagnosis);
    }

    @Override
    public void activate(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        /* Solo el vet que creó o admin pueden activar */
        if (!currentUser.getId().equals(diagnosis.getVet().getId()) && !hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only the veterinarian who created or admin can activate this diagnosis");
        }

        if (diagnosis.getActive()) {
            throw new IllegalStateException("Diagnosis is already active");
        }

        diagnosis.setActive(true);
        diagnosisRepository.save(diagnosis);
    }

    @Override
    public void deactivate(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        /* Solo el vet que creó o admin pueden desactivar */
        if (!currentUser.getId().equals(diagnosis.getVet().getId()) && !hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only the veterinarian who created or admin can deactivate this diagnosis");
        }

        if (!diagnosis.getActive()) {
            throw new IllegalStateException("Diagnosis is already inactive");
        }

        diagnosis.setActive(false);
        diagnosisRepository.save(diagnosis);
    }

    @Override
    public DiagnosisResponseDTO getById(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        if (!canAccessDiagnosis(diagnosis)) {
            throw new SecurityException("Unauthorized to view this diagnosis");
        }

        return diagnosisMapper.toResponseDTO(diagnosis);
    }

    @Override
    public List<DiagnosisResponseDTO> listAll() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        List<Diagnosis> diagnoses;
        if (hasRole(currentUser, "OWNER")) {
            diagnoses = diagnosisRepository.findAll().stream()
                    .filter(d -> d.getAppointment().getOwner().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        } else if (hasRole(currentUser, "VETERINARIAN") || hasRole(currentUser, "EMPLOYEE") || hasRole(currentUser, "ADMIN")) {
            diagnoses = diagnosisRepository.findAll();
        } else {
            throw new SecurityException("Unauthorized to list diagnoses");
        }
        return diagnoses.stream().map(diagnosisMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<DiagnosisResponseDTO> listByFilters(Long petId, Long vetId, LocalDate startDate, LocalDate endDate) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        if (!hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only admins can filter diagnoses");
        }

        List<Diagnosis> diagnoses = diagnosisRepository.findAll();
        if (petId != null) {
            diagnoses = diagnoses.stream()
                    .filter(d -> d.getAppointment().getPet().getId().equals(petId))
                    .collect(Collectors.toList());
        }
        if (vetId != null) {
            diagnoses = diagnoses.stream()
                    .filter(d -> d.getVet().getId().equals(vetId))
                    .collect(Collectors.toList());
        }
        if (startDate != null && endDate != null) {
            diagnoses = diagnoses.stream()
                    .filter(d -> !d.getDate().isBefore(startDate) && !d.getDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        return diagnoses.stream().map(diagnosisMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<DiagnosisResponseDTO> listMyDiagnoses() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        if (!hasRole(currentUser, "VETERINARIAN")) {
            throw new SecurityException("Only veterinarians can access this endpoint");
        }

        return diagnosisRepository.findByVet(currentUser).stream()
                .map(diagnosisMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private boolean canAccessDiagnosis(Diagnosis diagnosis) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        if (hasRole(currentUser, "VETERINARIAN") || hasRole(currentUser, "EMPLOYEE") || hasRole(currentUser, "ADMIN")) {
            return true;
        }
        return diagnosis.getAppointment().getOwner().getId().equals(currentUser.getId());
    }

    @Override
    public List<DiagnosisResponseDTO> listByPet(Long petId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (hasRole(currentUser, "OWNER") && !pet.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("You don't have permission to view diagnoses for this pet");
        }

        return diagnosisRepository.findByAppointment_Pet_Id(petId).stream()
                .filter(Diagnosis::getActive)
                .sorted((d1, d2) -> d2.getDate().compareTo(d1.getDate()))
                .map(diagnosisMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRole() != null && user.getRole().name().equals(roleName.toUpperCase());
    }

    @Override
    public byte[] generatePdf(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findByIdWithAllRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        if (!canAccessDiagnosis(diagnosis)) {
            throw new SecurityException("Unauthorized to view this diagnosis");
        }

        DiagnosisResponseDTO dto = diagnosisMapper.toResponseDTO(diagnosis);
        return pdfService.generateDiagnosisPdf(dto);
    }

}