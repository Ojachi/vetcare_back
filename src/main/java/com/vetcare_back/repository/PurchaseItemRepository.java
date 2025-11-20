package com.vetcare_back.repository;

import com.vetcare_back.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    List<PurchaseItem> findByPurchaseId(Long purchaseId);

    @Query("SELECT pi FROM PurchaseItem pi JOIN FETCH pi.product WHERE pi.purchase.id = :purchaseId")
    List<PurchaseItem> findByPurchaseIdWithProduct(@Param("purchaseId") Long purchaseId);

    @Query("SELECT pi FROM PurchaseItem pi WHERE pi.product.id = :productId ORDER BY pi.purchase.purchaseDate DESC")
    List<PurchaseItem> findByProductIdOrderByPurchaseDateDesc(@Param("productId") Long productId);

    @Query("SELECT SUM(pi.quantity) FROM PurchaseItem pi WHERE pi.product.id = :productId AND pi.purchase.status = 'COMPLETED'")
    Integer getTotalSoldQuantityByProductId(@Param("productId") Long productId);

    @Query("SELECT pi FROM PurchaseItem pi " +
           "WHERE pi.purchase.status = 'COMPLETED' " +
           "AND pi.purchase.purchaseDate BETWEEN :from AND :to")
    List<PurchaseItem> findCompletedItemsByDateRange(@Param("from") java.time.LocalDateTime from, 
                                                      @Param("to") java.time.LocalDateTime to);
}