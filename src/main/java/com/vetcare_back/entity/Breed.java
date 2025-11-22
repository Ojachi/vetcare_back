package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "breeds",
        indexes = {
                @Index(name = "idx_breeds_species", columnList = "species_id"),
                @Index(name = "idx_breeds_name", columnList = "name")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
