package com.vetcare_back.repository;

import com.vetcare_back.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
}