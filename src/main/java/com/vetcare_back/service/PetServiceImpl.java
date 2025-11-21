package com.vetcare_back.service;

import com.vetcare_back.dto.pet.PetDTO;
import com.vetcare_back.dto.pet.PetResponseDTO;
import com.vetcare_back.entity.Appointment;
import com.vetcare_back.entity.Diagnosis;
import com.vetcare_back.entity.Pet;
import com.vetcare_back.entity.User;
import com.vetcare_back.exception.UserNotFoundExeption;
import com.vetcare_back.mapper.PetMapper;
import com.vetcare_back.repository.AppointmentRepository;
import com.vetcare_back.repository.DiagnosisRepository;
import com.vetcare_back.repository.PetRepository;
import com.vetcare_back.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements IPetService{

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PetMapper petMapper;
    private final AppointmentRepository appointmentRepository;
    private final DiagnosisRepository diagnosisRepository;

    public PetServiceImpl(PetRepository petRepository, UserRepository userRepository, PetMapper petMapper,
                          AppointmentRepository appointmentRepository, DiagnosisRepository diagnosisRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.petMapper = petMapper;
        this.appointmentRepository = appointmentRepository;
        this.diagnosisRepository = diagnosisRepository;
    }

    @Override
    public PetResponseDTO create(PetDTO dto) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user is not found."));
        User owner;
        if(hasRole("OWNER")){
            if(dto.getOwnerId() != null && !dto.getOwnerId().equals(currentUser.getId())){
               throw new SecurityException("Users can only register their own pets");
            }
            owner = currentUser;
        } else if(hasRole("EMPLOYEE") || hasRole("ADMIN") || hasRole("VETERINARIAN")){
            if(dto.getOwnerId() == null){
                throw new SecurityException("Owner id is required for staff");
            }
            owner = userRepository.findById(dto.getOwnerId())
                    . orElseThrow(() -> new UserNotFoundExeption("Owner is not found."));
        } else {
            throw new SecurityException("Unauthorized to create pet");
        }

        Pet pet = petMapper.toEntity(dto);
        pet.setOwner(owner);
        pet.setActive(true);
        pet =  petRepository.save(pet);
        return petMapper.toResponseDTO(pet);
    }

    @Override
    public PetResponseDTO update(Long id, PetDTO dto) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (!canAccessPet(pet, false)) {
            throw new SecurityException("Unauthorized to update this pet");
        }

        petMapper.updateEntity(dto, pet);
        pet = petRepository.save(pet);
        return petMapper.toResponseDTO(pet);
    }

    @Override
    public void activate(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (!canAccessPet(pet, false)) {
            throw new SecurityException("Unauthorized to activate this pet");
        }

        if (pet.getActive()) {
            throw new IllegalStateException("Pet is already active");
        }

        pet.setActive(true);
        petRepository.save(pet);
    }

    @Override
    public void deactivate(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (!canAccessPet(pet, false)) {
            throw new SecurityException("Unauthorized to deactivate this pet");
        }

        if (!pet.getActive()) {
            throw new IllegalStateException("Pet is already inactive");
        }

        pet.setActive(false);
        petRepository.save(pet);
    }

    @Override
    public void delete(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (!canAccessPet(pet, true)) {
            throw new SecurityException("Unauthorized to delete this pet");
        }

        List<Appointment> appointments = appointmentRepository.findByPet(pet);
        if (!appointments.isEmpty()) {
            throw new IllegalStateException("Cannot delete pet with existing appointments. Found " + appointments.size() + " appointment(s).");
        }

        List<Diagnosis> diagnoses = diagnosisRepository.findByAppointment_Pet_Id(id);
        if (!diagnoses.isEmpty()) {
            throw new IllegalStateException("Cannot delete pet with existing diagnoses. Found " + diagnoses.size() + " diagnosis(es).");
        }

        pet.setActive(false);
        petRepository.save(pet);
    }

    @Override
    public PetResponseDTO getById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        if (!canAccessPet(pet, true)) {
            throw new SecurityException("Unauthorized to view this pet");
        }
        return petMapper.toResponseDTO(pet);
    }

    @Override
    public List<PetResponseDTO> listAll() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        List<Pet> pets;
        if (hasRole("OWNER")) {
            pets = petRepository.findByOwner(currentUser);
        } else if (hasRole("EMPLOYEE") || hasRole("VETERINARIAN") || hasRole("ADMIN")) {
            pets = petRepository.findAll();
        } else {
            throw new SecurityException("Unauthorized to list pets");
        }
        return pets.stream().map(petMapper::toResponseDTO).collect(Collectors.toList());
    }

    private boolean canAccessPet(Pet pet, boolean allowVets) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        if (hasRole("EMPLOYEE") || hasRole("ADMIN")) {
            return true;
        }
        if (allowVets && hasRole("VETERINARIAN")) {
            return true;
        }
        return pet.getOwner().getId().equals(currentUser.getId());
    }

    @Override
    public List<PetResponseDTO> listByOwner(Long ownerId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundExeption("Owner not found"));

        // OWNER solo puede ver sus propias mascotas
        if (hasRole("OWNER") && !currentUser.getId().equals(ownerId)) {
            throw new SecurityException("Unauthorized to view pets of other owners");
        }

        List<Pet> pets = petRepository.findByOwner(owner);
        return pets.stream().map(petMapper::toResponseDTO).collect(Collectors.toList());
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
