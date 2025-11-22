package com.vetcare_back.controller.species;

import com.vetcare_back.dto.species.BreedResponseDTO;
import com.vetcare_back.dto.species.SpeciesResponseDTO;
import com.vetcare_back.entity.Breed;
import com.vetcare_back.entity.Species;
import com.vetcare_back.repository.BreedRepository;
import com.vetcare_back.repository.SpeciesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/species")
public class SpeciesController {

    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;

    public SpeciesController(SpeciesRepository speciesRepository, BreedRepository breedRepository) {
        this.speciesRepository = speciesRepository;
        this.breedRepository = breedRepository;
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
}
