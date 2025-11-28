package com.vetcare_back.controller.species;

import com.vetcare_back.dto.species.BreedResponseDTO;
import com.vetcare_back.dto.species.SpeciesDTO;
import com.vetcare_back.dto.species.SpeciesResponseDTO;
import com.vetcare_back.entity.Breed;
import com.vetcare_back.entity.Species;
import com.vetcare_back.repository.BreedRepository;
import com.vetcare_back.repository.SpeciesRepository;
import com.vetcare_back.service.SpeciesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/species")
public class SpeciesController {

    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final SpeciesService speciesService;

    public SpeciesController(SpeciesRepository speciesRepository, BreedRepository breedRepository, SpeciesService speciesService) {
        this.speciesRepository = speciesRepository;
        this.breedRepository = breedRepository;
        this.speciesService = speciesService;
    }

    @GetMapping
    public ResponseEntity<List<SpeciesResponseDTO>> getAllSpecies() {
        List<SpeciesResponseDTO> species = speciesRepository.findByActiveTrue().stream()
                .map(s -> SpeciesResponseDTO.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .active(s.getActive())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(species);
    }

    @GetMapping("/{speciesId}/breeds")
    public ResponseEntity<List<BreedResponseDTO>> getBreedsBySpecies(@PathVariable Long speciesId) {
        List<BreedResponseDTO> breeds = breedRepository.findBySpeciesIdAndActiveTrue(speciesId).stream()
                .map(b -> BreedResponseDTO.builder()
                        .id(b.getId())
                        .name(b.getName())
                        .speciesId(b.getSpecies().getId())
                        .speciesName(b.getSpecies().getName())
                        .active(b.getActive())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(breeds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeciesResponseDTO> getSpeciesById(@PathVariable Long id) {
        return ResponseEntity.ok(speciesService.getSpeciesById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpeciesResponseDTO> createSpecies(@Valid @RequestBody SpeciesDTO dto) {
        return ResponseEntity.ok(speciesService.createSpecies(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpeciesResponseDTO> updateSpecies(@PathVariable Long id, @Valid @RequestBody SpeciesDTO dto) {
        return ResponseEntity.ok(speciesService.updateSpecies(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSpecies(@PathVariable Long id) {
        speciesService.deleteSpecies(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateSpecies(@PathVariable Long id) {
        speciesService.activateSpecies(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SpeciesResponseDTO>> getAllSpeciesAdmin() {
        return ResponseEntity.ok(speciesService.getAllSpecies());
    }
}
