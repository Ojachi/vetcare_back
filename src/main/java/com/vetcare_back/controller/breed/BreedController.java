package com.vetcare_back.controller.breed;

import com.vetcare_back.dto.breed.BreedDTO;
import com.vetcare_back.dto.breed.BreedResponseDTO;
import com.vetcare_back.service.BreedService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breeds")
@CrossOrigin(origins = "*")
public class BreedController {

    @Autowired
    private BreedService breedService;

    @GetMapping
    public ResponseEntity<List<BreedResponseDTO>> getBreedsBySpecies(@RequestParam Long speciesId) {
        return ResponseEntity.ok(breedService.getBreedsBySpecies(speciesId));
    }

    @GetMapping("/all-active")
    public ResponseEntity<List<BreedResponseDTO>> getAllActiveBreeds() {
        return ResponseEntity.ok(breedService.getAllActiveBreeds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BreedResponseDTO> getBreedById(@PathVariable Long id) {
        return ResponseEntity.ok(breedService.getBreedById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BreedResponseDTO> createBreed(@Valid @RequestBody BreedDTO dto) {
        return ResponseEntity.ok(breedService.createBreed(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BreedResponseDTO> updateBreed(@PathVariable Long id, @Valid @RequestBody BreedDTO dto) {
        return ResponseEntity.ok(breedService.updateBreed(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBreed(@PathVariable Long id) {
        breedService.deleteBreed(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateBreed(@PathVariable Long id) {
        breedService.activateBreed(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BreedResponseDTO>> getAllBreeds() {
        return ResponseEntity.ok(breedService.getAllBreeds());
    }
}
