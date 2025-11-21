package com.vetcare_back.dto.purchase;

import com.vetcare_back.entity.PurchaseStatus;
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
public class PurchaseResponseDTO {

    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private List<PurchaseItemResponseDTO> items;
    private BigDecimal totalAmount;
    private LocalDateTime purchaseDate;
    private PurchaseStatus status;
    private String notes;

    public static PurchaseResponseDTO fromEntity(com.vetcare_back.entity.Purchase purchase) {
        List<PurchaseItemResponseDTO> itemDTOs = purchase.getItems() != null ?
                purchase.getItems().stream()
                        .map(PurchaseItemResponseDTO::fromEntity)
                        .collect(java.util.stream.Collectors.toList()) :
                java.util.Collections.emptyList();

        return PurchaseResponseDTO.builder()
                .id(purchase.getId())
                .userId(purchase.getUser().getId())
                .userEmail(purchase.getUser().getEmail())
                .userName(purchase.getUser().getName())
                .items(itemDTOs)
                .totalAmount(purchase.getTotalAmount())
                .purchaseDate(purchase.getPurchaseDate())
                .status(purchase.getStatus())
                .notes(purchase.getNotes())
                .build();
    }
}