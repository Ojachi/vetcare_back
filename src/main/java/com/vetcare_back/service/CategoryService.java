package com.vetcare_back.service;

import com.vetcare_back.dto.category.CategoryDTO;
import com.vetcare_back.dto.category.CategoryResponseDTO;
import com.vetcare_back.entity.Product;
import com.vetcare_back.entity.ProductCategory;
import com.vetcare_back.repository.ProductCategoryRepository;
import com.vetcare_back.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public CategoryResponseDTO create(CategoryDTO dto) {
        if (categoryRepository.existsByNameAndActiveTrue(dto.getName())) {
            throw new IllegalArgumentException("Category with name '" + dto.getName() + "' already exists");
        }

        ProductCategory category = ProductCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .active(true)
                .build();

        category = categoryRepository.save(category);
        return CategoryResponseDTO.fromEntity(category);
    }

    public CategoryResponseDTO findById(Long id) {
        ProductCategory category = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found or inactive"));
        return CategoryResponseDTO.fromEntity(category);
    }

    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findByActiveTrue().stream()
                .map(CategoryResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO update(Long id, CategoryDTO dto) {
        ProductCategory category = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found or inactive"));

        if (!category.getName().equals(dto.getName()) && 
            categoryRepository.existsByNameAndActiveTrue(dto.getName())) {
            throw new IllegalArgumentException("Category with name '" + dto.getName() + "' already exists");
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category = categoryRepository.save(category);
        
        return CategoryResponseDTO.fromEntity(category);
    }

    public void delete(Long id) {
        ProductCategory category = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found or inactive"));

        List<Product> products = productRepository.findByCategoryIdAndActiveTrue(id);
        products.forEach(product -> product.setCategory(null));
        productRepository.saveAll(products);

        category.setActive(false);
        categoryRepository.save(category);
    }
}
