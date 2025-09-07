package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_appointment_vet_start", columnNames = {"vet_id", "start_datetime"}),
            @UniqueConstraint(name = "uk_appointment_pet_start", columnNames = {"pet_id", "start_datetime"}),

    },
    indexes = {
        @Index(name = "idx_appointments_vet", columnList = "vet_id"),
            @Index(name = "idx_appointments_pet", columnList = "pet_id"),
            @Index(name = "idx_appointments_start", columnList = "start_datetime"),
            @Index(name = "idx_appointments_status", columnList = "status")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Services service;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "scheduled_by_id")
    private User scheduledBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vet_id")
    private User vet;

    @Column(name = "start_datetime")
    private LocalDateTime startDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(length = 500)
    private String note;

    @Column(name = "create_at", nullable = false)
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();

}
