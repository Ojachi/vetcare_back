package com.vetcare_back.controller.service;

import com.vetcare_back.dto.service.ServiceDTO;
import com.vetcare_back.dto.service.ServiceResponseDTO;
import com.vetcare_back.service.IServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceController {

    private final IServiceService serviceService;

    public ServiceController(IServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping("/admin/services")
    public ResponseEntity<ServiceResponseDTO> create(@Valid @RequestBody ServiceDTO dto) {
        ServiceResponseDTO createdService = serviceService.create(dto);
        return ResponseEntity.ok(createdService);
    }

    @PutMapping("/admin/services/{id}")
    public ResponseEntity<ServiceResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ServiceDTO dto) {
        ServiceResponseDTO updatedService = serviceService.update(id, dto);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/admin/services/{id}")
    public ResponseEntity<ServiceDTO> delete(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/services/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        serviceService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceResponseDTO> getById(@PathVariable Long id) {
        ServiceResponseDTO service = serviceService.getById(id);
        return ResponseEntity.ok(service);
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponseDTO>> listAll() {
        List<ServiceResponseDTO> services = serviceService.listAll();
        return ResponseEntity.ok(services);
    }
}