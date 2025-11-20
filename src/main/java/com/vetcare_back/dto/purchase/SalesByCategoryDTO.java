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
public class SalesByCategoryDTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal revenue;
    private Long orderCount;
}
