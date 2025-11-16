package com.vetcare_back.service;

import com.vetcare_back.dto.diagnosis.DiagnosisDTO;
import com.vetcare_back.dto.diagnosis.DiagnosisResponseDTO;
import com.vetcare_back.entity.Diagnosis;

import java.time.LocalDate;
import java.util.List;

public interface IDiagnosisService {
    DiagnosisResponseDTO create(DiagnosisDTO dto);
    DiagnosisResponseDTO update(Long id, DiagnosisDTO dto);
    void activate(Long id);
    void deactivate(Long id);
    DiagnosisResponseDTO getById(Long id);
    List<DiagnosisResponseDTO> listAll();
    List<DiagnosisResponseDTO> listByFilters(Long petId, Long vetId, LocalDate startDate, LocalDate endDate);
    List<DiagnosisResponseDTO> listByPet(Long petId);
    List<DiagnosisResponseDTO> listMyDiagnoses();
    byte[] generatePdf(Long id);
}
