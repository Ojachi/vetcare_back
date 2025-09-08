package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pets",
    indexes = {
        @Index(name = "idx_pets_owner", columnList = "owner_id")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private String species;    // Especie

    @Column(nullable = false, length = 120)
    private String breed;      // Raza

    private int age;
    private Double weight;

    @Column(nullable = false, length = 20)
    private String sex;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
