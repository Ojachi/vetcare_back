package com.vetcare_back.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {

    private Long id;
    private List<CartItemResponseDTO> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartResponseDTO fromEntity(com.vetcare_back.entity.Cart cart) {
        if (cart == null) {
            return CartResponseDTO.builder()
                    .items(java.util.Collections.emptyList())
                    .totalAmount(java.math.BigDecimal.ZERO)
                    .totalItems(0)
                    .build();
        }

        List<CartItemResponseDTO> itemDTOs = cart.getItems() != null ?
                cart.getItems().stream()
                        .map(CartItemResponseDTO::fromEntity)
                        .collect(java.util.stream.Collectors.toList()) :
                java.util.Collections.emptyList();

        return CartResponseDTO.builder()
                .id(cart.getId())
                .items(itemDTOs)
                .totalAmount(cart.calculateTotal())
                .totalItems(cart.getTotalItems())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}