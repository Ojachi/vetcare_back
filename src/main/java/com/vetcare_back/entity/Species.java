package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "species",
        indexes = {
                @Index(name = "idx_species_name", columnList = "name")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
