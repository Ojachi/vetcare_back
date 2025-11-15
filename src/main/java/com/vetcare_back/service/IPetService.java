package com.vetcare_back.service;

import com.vetcare_back.dto.pet.PetDTO;
import com.vetcare_back.dto.pet.PetResponseDTO;
import com.vetcare_back.entity.Pet;

import java.util.List;

public interface IPetService {
    PetResponseDTO create(PetDTO dto);
    PetResponseDTO update(Long id, PetDTO dto);
    void activate(Long id);
    void deactivate(Long id);
    void delete(Long id);
    PetResponseDTO getById(Long id);
    List<PetResponseDTO> listAll();
}
