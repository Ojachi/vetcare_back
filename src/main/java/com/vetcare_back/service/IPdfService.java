package com.vetcare_back.service;

import com.vetcare_back.dto.diagnosis.DiagnosisResponseDTO;

public interface IPdfService {
    byte[] generateDiagnosisPdf(DiagnosisResponseDTO diagnosis);
}
