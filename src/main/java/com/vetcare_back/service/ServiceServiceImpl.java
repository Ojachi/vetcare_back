package com.vetcare_back.service;

import com.vetcare_back.dto.service.ServiceDTO;
import com.vetcare_back.dto.service.ServiceResponseDTO;
import com.vetcare_back.entity.Services;
import com.vetcare_back.mapper.ServiceMapper;
import com.vetcare_back.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceServiceImpl implements IServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    public ServiceServiceImpl(ServiceRepository serviceRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
    }

    @Override
    public ServiceResponseDTO create(ServiceDTO dto) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can create services");
        }
        if (serviceRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Service name already exists");
        }
        Services service = serviceMapper.toEntity(dto);
        service = serviceRepository.save(service);
        return serviceMapper.toResponseDTO(service);
    }

    @Override
    public ServiceResponseDTO update(Long id, ServiceDTO dto) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can update services");
        }
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        if (!service.getName().equals(dto.getName()) && serviceRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Service name already exists");
        }
        serviceMapper.updateEntity(dto, service);
        service = serviceRepository.save(service);
        return serviceMapper.toResponseDTO(service);
    }

    @Override
    public void deactivate(Long id) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can deactivate services");
        }
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        service.setActive(false);
        serviceRepository.save(service);
    }

    @Override
    public void delete(Long id) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can delete services");
        }
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        serviceRepository.delete(service);
    }

    @Override
    public ServiceResponseDTO getById(Long id) {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        return serviceMapper.toResponseDTO(service);
    }

    @Override
    public List<ServiceResponseDTO> listAll() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}