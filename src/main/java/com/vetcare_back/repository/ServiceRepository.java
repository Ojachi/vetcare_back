package com.vetcare_back.repository;

import com.vetcare_back.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Services, Long> {
    Optional<Services> findByName(String name);
}
