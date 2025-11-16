package com.vetcare_back.repository;

import com.vetcare_back.entity.Appointment;
import com.vetcare_back.entity.Diagnosis;
import com.vetcare_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository  extends JpaRepository<Diagnosis,Long> {
    Optional<Diagnosis> findByAppointment(Appointment appointment);
    List<Diagnosis> findByVet(User vet);
    List<Diagnosis> findByAppointment_Pet_Id(Long petId);
    
    @Query("SELECT d FROM Diagnosis d " +
           "JOIN FETCH d.appointment a " +
           "JOIN FETCH a.pet p " +
           "JOIN FETCH p.owner " +
           "JOIN FETCH a.service " +
           "JOIN FETCH d.vet " +
           "WHERE d.id = :id")
    Optional<Diagnosis> findByIdWithAllRelations(@Param("id") Long id);
}
