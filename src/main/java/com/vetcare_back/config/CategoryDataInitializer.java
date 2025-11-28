package com.vetcare_back.config;

import com.vetcare_back.entity.ProductCategory;
import com.vetcare_back.repository.ProductCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@org.springframework.core.annotation.Order(1)
public class CategoryDataInitializer {

    private final ProductCategoryRepository categoryRepository;

    public CategoryDataInitializer(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (categoryRepository.count() == 0) {
            log.info("Initializing product categories...");
            
            createCategory("Alimentos", "Alimentos balanceados, snacks y premios para mascotas");
            createCategory("Medicamentos", "Medicamentos veterinarios, antibióticos y tratamientos");
            createCategory("Higiene y Cuidado", "Productos de limpieza, shampoos y accesorios de aseo");
            createCategory("Accesorios", "Collares, correas, juguetes y camas para mascotas");
            createCategory("Suplementos", "Vitaminas, suplementos nutricionales y complementos");
            createCategory("Antiparasitarios", "Productos contra pulgas, garrapatas y parásitos");
            
            log.info("✅ {} categories initialized successfully", categoryRepository.count());
        } else {
            log.info("Categories already exist - skipping initialization");
        }
    }

    private void createCategory(String name, String description) {
        ProductCategory category = ProductCategory.builder()
                .name(name)
                .description(description)
                .active(true)
                .build();
        categoryRepository.save(category);
    }
}
