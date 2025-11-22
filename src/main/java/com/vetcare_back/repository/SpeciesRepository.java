package com.vetcare_back.repository;

import com.vetcare_back.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {
    
    List<Species> findByActiveTrue();
    
    Optional<Species> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT COUNT(p) FROM Pet p WHERE p.species.id = :speciesId AND p.active = true")
    Long countActivePetsBySpeciesId(Long speciesId);
}
