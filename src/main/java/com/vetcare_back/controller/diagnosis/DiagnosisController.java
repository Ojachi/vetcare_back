package com.vetcare_back.controller.diagnosis;

import com.vetcare_back.dto.diagnosis.DiagnosisDTO;
import com.vetcare_back.dto.diagnosis.DiagnosisResponseDTO;
import com.vetcare_back.service.IDiagnosisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
public class DiagnosisController {

    private final IDiagnosisService diagnosisService;

    public DiagnosisController(IDiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping
    public ResponseEntity<DiagnosisResponseDTO> create(@Valid @RequestBody DiagnosisDTO dto) {
        return ResponseEntity.ok(diagnosisService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiagnosisResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DiagnosisDTO dto) {
        return ResponseEntity.ok(diagnosisService.update(id, dto));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        diagnosisService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        diagnosisService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosisService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<DiagnosisResponseDTO>> listAll() {
        return ResponseEntity.ok(diagnosisService.listAll());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<DiagnosisResponseDTO>> listByFilters(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long vetId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        return ResponseEntity.ok(diagnosisService.listByFilters(petId, vetId, start, end));
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<DiagnosisResponseDTO>> getByPetId(@PathVariable Long petId) {
        return ResponseEntity.ok(diagnosisService.listByPet(petId));
    }

    @GetMapping("/my-diagnoses")
    public ResponseEntity<List<DiagnosisResponseDTO>> getMyDiagnoses() {
        return ResponseEntity.ok(diagnosisService.listMyDiagnoses());
    }
}