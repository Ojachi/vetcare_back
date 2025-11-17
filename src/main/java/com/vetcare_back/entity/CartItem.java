package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"})
        },
        indexes = {
                @Index(name = "idx_cart_items_cart", columnList = "cart_id"),
                @Index(name = "idx_cart_items_product", columnList = "product_id")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "added_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Método de utilidad para calcular subtotal
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (price == null && product != null) {
            price = product.getPrice();
        }
    }
}