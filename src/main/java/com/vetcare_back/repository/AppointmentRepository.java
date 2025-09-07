package com.vetcare_back.repository;

import com.vetcare_back.entity.Appointment;
import com.vetcare_back.entity.AppointmentStatus;
import com.vetcare_back.entity.Pet;
import com.vetcare_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    List <Appointment> findByVet(User vet);
    List <Appointment> findByPet(Pet pet);
    List<Appointment> findByOwner(User owner);
    List<Appointment> findByStatus(AppointmentStatus status);
    //List<Appointment> findByAppointmentDate(LocalDate date);
}
