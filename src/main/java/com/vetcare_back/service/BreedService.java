package com.vetcare_back.service;

import com.vetcare_back.dto.breed.BreedDTO;
import com.vetcare_back.dto.breed.BreedResponseDTO;
import com.vetcare_back.entity.Breed;
import com.vetcare_back.entity.Species;
import com.vetcare_back.repository.BreedRepository;
import com.vetcare_back.repository.SpeciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BreedService {

    @Autowired
    private BreedRepository breedRepository;

    @Autowired
    private SpeciesRepository speciesRepository;

    public BreedResponseDTO createBreed(BreedDTO dto) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can create breeds");
        }
        Species species = speciesRepository.findById(dto.getSpeciesId())
                .orElseThrow(() -> new RuntimeException("Species not found"));

        if (!species.getActive()) {
            throw new RuntimeException("Cannot create breed for inactive species");
        }

        if (breedRepository.existsByNameAndSpeciesId(dto.getName(), dto.getSpeciesId())) {
            throw new RuntimeException("Breed '" + dto.getName() + "' already exists for this species");
        }

        Breed breed = Breed.builder()
                .name(dto.getName())
                .species(species)
                .active(true)
                .build();

        breed = breedRepository.save(breed);
        return BreedResponseDTO.fromEntity(breed);
    }

    public BreedResponseDTO updateBreed(Long id, BreedDTO dto) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can update breeds");
        }
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found"));

        Species species = speciesRepository.findById(dto.getSpeciesId())
                .orElseThrow(() -> new RuntimeException("Species not found"));

        if (!species.getActive()) {
            throw new RuntimeException("Cannot assign breed to inactive species");
        }

        if (!breed.getName().equals(dto.getName()) || !breed.getSpecies().getId().equals(dto.getSpeciesId())) {
            if (breedRepository.existsByNameAndSpeciesId(dto.getName(), dto.getSpeciesId())) {
                throw new RuntimeException("Breed '" + dto.getName() + "' already exists for this species");
            }
        }

        breed.setName(dto.getName());
        breed.setSpecies(species);
        breed = breedRepository.save(breed);
        return BreedResponseDTO.fromEntity(breed);
    }

    public void deleteBreed(Long id) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can delete breeds");
        }
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found"));

        Long petCount = breedRepository.countActivePetsByBreedId(id);
        if (petCount > 0) {
            throw new RuntimeException("Cannot delete breed with " + petCount + " active pet(s) associated");
        }

        breed.setActive(false);
        breedRepository.save(breed);
    }

    public void activateBreed(Long id) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can activate breeds");
        }
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found"));

        if (!breed.getSpecies().getActive()) {
            throw new RuntimeException("Cannot activate breed with inactive species");
        }

        breed.setActive(true);
        breedRepository.save(breed);
    }

    public BreedResponseDTO getBreedById(Long id) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found"));
        return BreedResponseDTO.fromEntity(breed);
    }

    public List<BreedResponseDTO> getBreedsBySpecies(Long speciesId) {
        return breedRepository.findBySpeciesIdAndActiveTrue(speciesId).stream()
                .map(BreedResponseDTO::fromEntity)
                .toList();
    }

    public List<BreedResponseDTO> getAllActiveBreeds() {
        return breedRepository.findByActiveTrue().stream()
                .map(BreedResponseDTO::fromEntity)
                .toList();
    }

    public List<BreedResponseDTO> getAllBreeds() {
        return breedRepository.findAll().stream()
                .map(BreedResponseDTO::fromEntity)
                .toList();
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
