package com.vetcare_back.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemResponseDTO {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public static PurchaseItemResponseDTO fromEntity(com.vetcare_back.entity.PurchaseItem item) {
        return PurchaseItemResponseDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}