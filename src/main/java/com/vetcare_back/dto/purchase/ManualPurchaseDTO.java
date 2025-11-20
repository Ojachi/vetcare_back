package com.vetcare_back.dto.purchase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualPurchaseDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<ManualPurchaseItemDTO> items;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
