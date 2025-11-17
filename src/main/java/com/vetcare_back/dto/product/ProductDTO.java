package com.vetcare_back.dto.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Description must be less than 100 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Pattern(regexp = "^(data:image/(png|jpeg|jpg);base64,[A-Za-z0-9+/=]+)?$", message = "Image must be a valid Base64 string (PNG/JPEG) or null")
    private String image;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}