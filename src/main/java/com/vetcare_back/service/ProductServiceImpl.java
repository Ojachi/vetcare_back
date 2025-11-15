package com.vetcare_back.service;

import com.vetcare_back.dto.product.ProductDTO;
import com.vetcare_back.dto.product.ProductResponseDTO;
import com.vetcare_back.entity.Product;
import com.vetcare_back.entity.User;
import com.vetcare_back.exception.UserNotFoundExeption;
import com.vetcare_back.mapper.ProductMapper;
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

    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productMapper = productMapper;
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
        product = productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or inactive"));
        return productMapper.toResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .filter(Product::getActive)
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
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid Base64 string", e);
            }
        }
    }
}