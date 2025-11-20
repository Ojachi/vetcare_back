package com.vetcare_back.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private Integer stock;
    private Boolean active;
    private Long categoryId;
    private String categoryName;
}
