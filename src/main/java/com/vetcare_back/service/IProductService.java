package com.vetcare_back.service;

import com.vetcare_back.dto.product.ProductDTO;
import com.vetcare_back.dto.product.ProductResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    ProductResponseDTO create(ProductDTO dto, MultipartFile image);
    ProductResponseDTO findById(Long id);
    List<ProductResponseDTO> findAll();
    List<ProductResponseDTO> findByCategory(Long categoryId);
    ProductResponseDTO update(Long id, ProductDTO dto, MultipartFile image);
    void activate(Long id);
    void delete(Long id);
}