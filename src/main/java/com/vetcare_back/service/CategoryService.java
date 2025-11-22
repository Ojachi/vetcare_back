package com.vetcare_back.service;

import com.vetcare_back.dto.category.CategoryDTO;
import com.vetcare_back.dto.category.CategoryResponseDTO;
import com.vetcare_back.entity.Product;
import com.vetcare_back.entity.ProductCategory;
import com.vetcare_back.repository.ProductCategoryRepository;
import com.vetcare_back.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can create categories");
        }
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
        boolean debug = "true".equals(System.getenv("DEBUG_PERFORMANCE"));
        long start = 0;
        
        if (debug) {
            System.out.println("\n🔍 ========== CATEGORIES FINDALL START ==========");
            start = System.currentTimeMillis();
        }
        
        List<CategoryResponseDTO> result = categoryRepository.findByActiveTrue().stream()
                .map(CategoryResponseDTO::fromEntity)
                .collect(Collectors.toList());
        
        if (debug) {
            System.out.println("⏱️ TOTAL findAll(): " + (System.currentTimeMillis() - start) + "ms");
            System.out.println("📦 Categories found: " + result.size());
            System.out.println("🔍 ========== CATEGORIES FINDALL END ==========\n");
        }
        
        return result;
    }

    public CategoryResponseDTO update(Long id, CategoryDTO dto) {
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can update categories");
        }
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
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can delete categories");
        }
        ProductCategory category = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found or inactive"));

        List<Product> products = productRepository.findByCategoryIdAndActiveTrue(id);
        products.forEach(product -> product.setCategory(null));
        productRepository.saveAll(products);

        category.setActive(false);
        categoryRepository.save(category);
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
