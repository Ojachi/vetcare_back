package com.vetcare_back.repository;

import com.vetcare_back.entity.Appointment;
import com.vetcare_back.entity.Diagnosis;
import com.vetcare_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository  extends JpaRepository<Diagnosis,Long> {
    Optional<Diagnosis> findByAppointment(Appointment appointment);
    List<Diagnosis> findByVet(User vet);
}
