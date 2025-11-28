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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final ProductCategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository, ProductMapper productMapper, ProductCategoryRepository categoryRepository, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public ProductResponseDTO create(ProductDTO dto, MultipartFile image) {
        User currentUser = getCurrentUser();

        if (!hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only admins can create products");
        }

        Product product = productMapper.toEntity(dto);
        
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findByIdAndActiveTrue(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found or inactive")));
        }

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(image, "vetcare/products");
                product.setImage(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading image", e);
            }
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
        
        // Verificar si el usuario está autenticado
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAuthenticated = !"anonymousUser".equals(currentEmail);
        
        Product product;
        if (isAuthenticated) {
            User currentUser = userRepository.findByEmail(currentEmail).orElse(null);
            if (currentUser != null && hasRole(currentUser, "ADMIN")) {
                // Admin puede ver productos inactivos
                product = productRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            } else {
                // Usuario autenticado - solo productos activos
                product = productRepository.findById(id)
                        .filter(Product::getActive)
                        .orElseThrow(() -> new EntityNotFoundException("Product not found or inactive"));
            }
        } else {
            // Usuario no autenticado - solo productos activos
            product = productRepository.findById(id)
                    .filter(Product::getActive)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found or inactive"));
        }
        
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
        long totalStart = 0, step1 = 0, step2 = 0;
        
        if (debug) {
            System.out.println("\n🔍 ========== PRODUCTS FINDALL START ==========");
            totalStart = System.currentTimeMillis();
            step1 = System.currentTimeMillis();
        }
        
        // Verificar si el usuario está autenticado
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAuthenticated = !"anonymousUser".equals(currentEmail);
        
        List<Product> products;
        if (isAuthenticated) {
            User currentUser = userRepository.findByEmail(currentEmail).orElse(null);
            if (currentUser != null && hasRole(currentUser, "ADMIN")) {
                products = productRepository.findAllWithCategory();
            } else {
                products = productRepository.findAllActiveWithCategory();
            }
        } else {
            // Usuario no autenticado - solo productos activos
            products = productRepository.findAllActiveWithCategory();
        }
        
        if (debug) {
            System.out.println("⏱️ Step 1 - findAllWithCategory(): " + (System.currentTimeMillis() - step1) + "ms");
            System.out.println("📦 Products found: " + products.size());
            step2 = System.currentTimeMillis();
        }
        
        List<ProductResponseDTO> result = products.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        if (debug) {
            System.out.println("⏱️ Step 2 - mapping to DTO: " + (System.currentTimeMillis() - step2) + "ms");
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
    public ProductResponseDTO update(Long id, ProductDTO dto, MultipartFile image) {
        User currentUser = getCurrentUser();

        if (!hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only admins can update products");
        }

        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or inactive"));

        String oldImageUrl = product.getImage();

        productMapper.updateEntity(dto, product);
        
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findByIdAndActiveTrue(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found or inactive")));
        } else {
            product.setCategory(null);
        }

        if (image != null && !image.isEmpty()) {
            try {
                String newImageUrl = cloudinaryService.uploadImage(image, "vetcare/products");
                product.setImage(newImageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading image", e);
            }
        }
        
        product = productRepository.save(product);
        
        // Eliminar imagen anterior solo después del commit exitoso
        if (image != null && !image.isEmpty() && oldImageUrl != null) {
            try {
                cloudinaryService.deleteImage(oldImageUrl);
            } catch (Exception e) {
                // Log error pero no falla la operación
                System.err.println("Warning: Failed to delete old image from Cloudinary: " + e.getMessage());
            }
        }
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

        if (!hasRole(currentUser, "ADMIN")) {
            throw new SecurityException("Only admins can delete products");
        }

        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or inactive"));

        product.setActive(false);
        productRepository.save(product);
        
        // Eliminar imagen de Cloudinary después del commit
        if (product.getImage() != null) {
            try {
                cloudinaryService.deleteImage(product.getImage());
            } catch (Exception e) {
                // Log error pero no falla la operación
                System.err.println("Warning: Failed to delete image from Cloudinary: " + e.getMessage());
            }
        }
    }

    private User getCurrentUser() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundExeption("Current user not found"));
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRole() != null && user.getRole().name().equals(roleName.toUpperCase());
    }
}