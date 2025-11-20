package com.vetcare_back.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private String period;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private BigDecimal totalAmount;
    private Long totalOrders;
    private BigDecimal averageOrderValue;
    private Map<String, Long> salesByStatus;
    private List<TopSellingProductDTO> topSellingProducts;
    private List<SalesByCategoryDTO> salesByCategory;
}
