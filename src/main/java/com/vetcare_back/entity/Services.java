package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services",
    indexes = {
        @Index(name = "idx_services_name", columnList = "name")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;    //

    @Column(length = 500)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "requires_veterinarian", nullable = false)
    @Builder.Default
    private Boolean requiresVeterinarian = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
