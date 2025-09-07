package com.vetcare_back.repository;

import com.vetcare_back.entity.Pet;
import com.vetcare_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet,Long> {
    List<Pet> findByOwner(User owner);
}
