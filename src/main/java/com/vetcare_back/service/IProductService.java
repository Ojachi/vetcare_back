package com.vetcare_back.service;

import com.vetcare_back.dto.product.ProductDTO;
import com.vetcare_back.dto.product.ProductResponseDTO;

import java.util.List;

public interface IProductService {
    ProductResponseDTO create(ProductDTO dto);
    ProductResponseDTO findById(Long id);
    List<ProductResponseDTO> findAll();
    ProductResponseDTO update(Long id, ProductDTO dto);
    void activate(Long id);
    void delete(Long id);
}