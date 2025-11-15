package com.vetcare_back.service;
import com.vetcare_back.dto.service.ServiceDTO;
import com.vetcare_back.dto.service.ServiceResponseDTO;
import java.util.List;

public interface IServiceService {
    ServiceResponseDTO create(ServiceDTO dto);
    ServiceResponseDTO update(Long id, ServiceDTO dto);
    void activate(Long id);
    void deactivate(Long id);
    void delete(Long id);
    ServiceResponseDTO getById(Long id);
    List<ServiceResponseDTO> listAll();
}
