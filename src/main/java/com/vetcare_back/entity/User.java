package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table( name = "users",
        indexes = {
            @Index(name = "idx_users_email", columnList = "email")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false, unique = true, length = 180)
    private String email;

    @Column (nullable = false,  length = 255)
    private String password;

    @Column(length = 40)
    private String phone;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
