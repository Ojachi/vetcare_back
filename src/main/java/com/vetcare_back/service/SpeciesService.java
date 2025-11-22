package com.vetcare_back.service;

import com.vetcare_back.dto.species.SpeciesDTO;
import com.vetcare_back.dto.species.SpeciesResponseDTO;
import com.vetcare_back.entity.Species;
import com.vetcare_back.repository.SpeciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpeciesService {

    @Autowired
    private SpeciesRepository speciesRepository;

    @Autowired
    private BreedRepository breedRepository;

    public SpeciesResponseDTO createSpecies(SpeciesDTO dto) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can create species");
        }
        if (speciesRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Species with name '" + dto.getName() + "' already exists");
        }

        Species species = Species.builder()
                .name(dto.getName())
                .active(true)
                .build();

        species = speciesRepository.save(species);
        return SpeciesResponseDTO.fromEntity(species);
    }

    public SpeciesResponseDTO updateSpecies(Long id, SpeciesDTO dto) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can update species");
        }
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Species not found"));

        if (!species.getName().equals(dto.getName()) && speciesRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Species with name '" + dto.getName() + "' already exists");
        }

        species.setName(dto.getName());
        species = speciesRepository.save(species);
        return SpeciesResponseDTO.fromEntity(species);
    }

    public void deleteSpecies(Long id) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can delete species");
        }
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Species not found"));

        Long petCount = speciesRepository.countActivePetsBySpeciesId(id);
        
        Long breedPetCount = breedRepository.findBySpeciesIdAndActiveTrue(id).stream()
                .mapToLong(breed -> breedRepository.countActivePetsByBreedId(breed.getId()))
                .sum();
        
        Long totalPetCount = petCount + breedPetCount;
        if (totalPetCount > 0) {
            throw new RuntimeException("Cannot delete species with " + totalPetCount + " active pet(s) associated");
        }

        species.setActive(false);
        speciesRepository.save(species);
    }

    public void activateSpecies(Long id) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can activate species");
        }
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Species not found"));

        species.setActive(true);
        speciesRepository.save(species);
    }

    public SpeciesResponseDTO getSpeciesById(Long id) {
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Species not found"));
        return SpeciesResponseDTO.fromEntity(species);
    }

    public List<SpeciesResponseDTO> getAllActiveSpecies() {
        return speciesRepository.findByActiveTrue().stream()
                .map(SpeciesResponseDTO::fromEntity)
                .toList();
    }

    public List<SpeciesResponseDTO> getAllSpecies() {
        return speciesRepository.findAll().stream()
                .map(SpeciesResponseDTO::fromEntity)
                .toList();
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
