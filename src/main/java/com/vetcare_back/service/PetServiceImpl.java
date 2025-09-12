package com.vetcare_back.service;

import com.vetcare_back.dto.pet.PetDTO;
import com.vetcare_back.dto.pet.PetResponseDTO;
import com.vetcare_back.entity.Pet;
import com.vetcare_back.entity.User;
import com.vetcare_back.exception.UserNotFoundExeption;
import com.vetcare_back.mapper.PetMapper;
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

    public PetServiceImpl(PetRepository petRepository, UserRepository userRepository, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.petMapper = petMapper;
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
    public void delete(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        if (!canAccessPet(pet, true)) {
            throw new SecurityException("Unauthorized to delete this pet");
        }

        petRepository.delete(pet);
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

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
