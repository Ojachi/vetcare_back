package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pets",
    indexes = {
        @Index(name = "idx_pets_owner", columnList = "owner_id"),
        @Index(name = "idx_pets_species", columnList = "species_id"),
        @Index(name = "idx_pets_breed", columnList = "breed_id")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(length = 100)
    private String customSpecies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private Breed breed;

    @Column(length = 120)
    private String customBreed;

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
