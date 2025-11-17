package com.vetcare_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases",
        indexes = {
                @Index(name = "idx_purchases_user", columnList = "user_id"),
                @Index(name = "idx_purchases_date", columnList = "purchase_date"),
                @Index(name = "idx_purchases_status", columnList = "status")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PurchaseItem> items = new ArrayList<>();

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "purchase_date", nullable = false)
    @Builder.Default
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PurchaseStatus status = PurchaseStatus.PENDING;

    @Column(length = 500)
    private String notes;

    // Métodos de utilidad
    public void addItem(PurchaseItem item) {
        items.add(item);
        item.setPurchase(this);
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(PurchaseItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void complete() {
        this.status = PurchaseStatus.COMPLETED;
    }

    public void cancel() {
        this.status = PurchaseStatus.CANCELLED;
    }

    public boolean isPending() {
        return PurchaseStatus.PENDING.equals(this.status);
    }

    public boolean isCompleted() {
        return PurchaseStatus.COMPLETED.equals(this.status);
    }
}