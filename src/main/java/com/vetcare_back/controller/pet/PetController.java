package com.vetcare_back.controller.pet;

import com.vetcare_back.dto.pet.PetDTO;
import com.vetcare_back.dto.pet.PetResponseDTO;
import com.vetcare_back.service.IPetService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final IPetService petService;

    public PetController(IPetService petService) {
        this.petService = petService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PetResponseDTO> create(@Valid @RequestBody PetDTO dto) {
        return ResponseEntity.ok(petService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PetDTO dto) {
        return ResponseEntity.ok(petService.update(id, dto));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        petService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        petService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> listAll() {
        return ResponseEntity.ok(petService.listAll());
    }
}