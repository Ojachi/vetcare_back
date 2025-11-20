package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_categories",
        indexes = {
                @Index(name = "idx_category_name", columnList = "name")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
