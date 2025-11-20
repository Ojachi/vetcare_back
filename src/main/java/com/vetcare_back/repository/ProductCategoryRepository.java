package com.vetcare_back.repository;

import com.vetcare_back.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    List<ProductCategory> findByActiveTrue();
    
    Optional<ProductCategory> findByIdAndActiveTrue(Long id);
    
    boolean existsByNameAndActiveTrue(String name);
}
