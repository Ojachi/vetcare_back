package com.vetcare_back.repository;

import com.vetcare_back.entity.Appointment;
import com.vetcare_back.entity.AppointmentStatus;
import com.vetcare_back.entity.Pet;
import com.vetcare_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    List <Appointment> findByAssignedTo(User AssignedTo);
    List <Appointment> findByPet(Pet pet);
    List<Appointment> findByOwner(User owner);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByStartDateTimeBetween(LocalDateTime start,LocalDateTime end);
    List<Appointment> findByAssignedToAndStartDateTimeBetween(User assignedTo, LocalDateTime start, LocalDateTime end);
    boolean existsByAssignedToAndStartDateTime(User assignedTo, LocalDateTime startDateTime);
    List<Appointment> findByAssignedToAndStartDateTime(User assignedTo, LocalDateTime startDateTime);
}
