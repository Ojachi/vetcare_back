package com.vetcare_back.repository;

import com.vetcare_back.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
    
    List<Breed> findBySpeciesIdAndActiveTrue(Long speciesId);
    
    List<Breed> findByActiveTrue();
    
    Optional<Breed> findByNameAndSpeciesId(String name, Long speciesId);
    
    boolean existsByNameAndSpeciesId(String name, Long speciesId);
    
    @Query("SELECT COUNT(p) FROM Pet p WHERE p.breed.id = :breedId AND p.active = true")
    Long countActivePetsByBreedId(Long breedId);
}
