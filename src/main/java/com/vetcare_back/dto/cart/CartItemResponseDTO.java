package com.vetcare_back.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private LocalDateTime addedAt;
    private Integer availableStock;

    public static CartItemResponseDTO fromEntity(com.vetcare_back.entity.CartItem item) {
        return CartItemResponseDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productDescription(item.getProduct().getDescription())
                .productImage(item.getProduct().getImage())
                .quantity(item.getQuantity())
                .unitPrice(item.getPrice())
                .subtotal(item.getSubtotal())
                .addedAt(item.getCreatedAt())
                .availableStock(item.getProduct().getStock())
                .build();
    }
}