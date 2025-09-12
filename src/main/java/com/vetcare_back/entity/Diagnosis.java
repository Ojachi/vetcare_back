package com.vetcare_back.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "diagnoses",
        indexes = {
                @Index(name = "idx_diagnoses_appointment", columnList = "appointment_id"),
                @Index(name = "idx_diagnoses_vet", columnList = "vet_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Relación 1:1 con la cita (un diagnóstico por cita) */
    @OneToOne(optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    /* Quien emitió el diagnóstico */
    @ManyToOne(optional = false)
    @JoinColumn(name = "vet_id", nullable = false)
    private User vet;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(length = 1000)
    private String treatment;

    @Column(length = 1000)
    private String medications;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
