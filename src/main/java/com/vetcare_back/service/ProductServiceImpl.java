package com.vetcare_back.service;

import com.vetcare_back.dto.product.ProductDTO;
import com.vetcare_back.dto.product.ProductResponseDTO;
import com.vetcare_back.entity.Product;
import com.vetcare_back.entity.ProductCategory;
import com.vetcare_back.entity.User;
import com.vetcare_back.exception.UserNotFoundExeption;
import com.vetcare_back.mapper.ProductMapper;
import com.vetcare_back.repository.ProductCategoryRepository;
import com.vetcare_back.repository.ProductRepository;
import com.vetcare_back.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final ProductCategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository, ProductMapper productMapper, ProductCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductResponseDTO create(ProductDTO dto) {
        User currentUser = getCurrentUser();

        // Solo ADMIN puede crear
        if (!hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only admins can create products");
        }

        validateBase64(dto.getImage());

        Product product = productMapper.toEntity(dto);
        
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findByIdAndActiveTrue(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found or inactive")));
        }
        
        product = productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        boolean debug = "true".equals(System.getenv("DEBUG_PERFORMANCE"));
        long start = 0;
        
        if (debug) {
            System.out.println("\n🔍 ========== PRODUCT FINDBYID START (id=" + id + ") ==========");
            start = System.currentTimeMillis();
        }
        
        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or inactive"));
        
        ProductResponseDTO result = productMapper.toResponseDTO(product);
        
        if (debug) {
            System.out.println("⏱️ TOTAL findById(): " + (System.currentTimeMillis() - start) + "ms");
            System.out.println("🔍 ========== PRODUCT FINDBYID END ==========\n");
        }
        
        return result;
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        boolean debug = "true".equals(System.getenv("DEBUG_PERFORMANCE"));
        long totalStart = 0, step1 = 0, step2 = 0, step3 = 0;
        
        if (debug) {
            System.out.println("\n🔍 ========== PRODUCTS FINDALL START ==========");
            totalStart = System.currentTimeMillis();
            step1 = System.currentTimeMillis();
        }
        
        User currentUser = getCurrentUser();
        
        if (debug) {
            System.out.println("⏱️ Step 1 - getCurrentUser(): " + (System.currentTimeMillis() - step1) + "ms");
            step2 = System.currentTimeMillis();
        }
        
        List<Product> products;
        if (hasRole(currentUser, "ADMIN")) {
            products = productRepository.findAllWithCategory();
        } else {
            products = productRepository.findAllActiveWithCategory();
        }
        
        if (debug) {
            System.out.println("⏱️ Step 2 - findAllWithCategory(): " + (System.currentTimeMillis() - step2) + "ms");
            System.out.println("📦 Products found: " + products.size());
            step3 = System.currentTimeMillis();
        }
        
        List<ProductResponseDTO> result = products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        if (debug) {
            System.out.println("⏱️ Step 3 - mapping to DTO: " + (System.currentTimeMillis() - step3) + "ms");
            System.out.println("⏱️ TOTAL findAll(): " + (System.currentTimeMillis() - totalStart) + "ms");
            System.out.println("🔍 ========== PRODUCTS FINDALL END ==========\n");
        }
        
        return result;
    }

    @Override
    public List<ProductResponseDTO> findByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrueWithCategory(categoryId).stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO update(Long id, ProductDTO dto) {
        User currentUser = getCurrentUser();

        // Solo ADMIN puede actualizar
        if (!hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only admins can update products");
        }

        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or inactive"));

        if (dto.getImage() != null) {
            validateBase64(dto.getImage());
        }

        productMapper.updateEntity(dto, product);
        
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findByIdAndActiveTrue(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found or inactive")));
        } else {
            product.setCategory(null);
        }
        
        product = productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    @Override
    public void activate(Long id) {
        User currentUser = getCurrentUser();

        if (!hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only admins can activate products");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getActive()) {
            throw new IllegalStateException("Product is already active");
        }

        product.setActive(true);
        productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        User currentUser = getCurrentUser();

        // Solo ADMIN puede eliminar (soft delete)
        if (!hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only admins can delete products");
        }

        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or inactive"));

        product.setActive(false);
        productRepository.save(product);
    }

    private User getCurrentUser() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRole() != null && user.getRole().name().equals(roleName.toUpperCase());
    }

    private void validateBase64(String base64) {
        if (base64 != null && !base64.isEmpty()) {
            try {
                if (!base64.matches("^data:image/(png|jpeg|jpg);base64,[A-Za-z0-9+/=]+$")) {
                    throw new IllegalArgumentException("Invalid Base64 image format");
                }
                String data = base64.split(",")[1];
                Base64.getDecoder().decode(data);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid Base64 string", e);
            }
        }
    }
}