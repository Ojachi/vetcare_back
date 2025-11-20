package com.vetcare_back.repository;

import com.vetcare_back.entity.Purchase;
import com.vetcare_back.entity.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByUserIdOrderByPurchaseDateDesc(Long userId);

    Page<Purchase> findByUserIdOrderByPurchaseDateDesc(Long userId, Pageable pageable);

    List<Purchase> findByStatus(PurchaseStatus status);

    List<Purchase> findByUserIdAndStatus(Long userId, PurchaseStatus status);

    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.items pi LEFT JOIN FETCH pi.product WHERE p.id = :id")
    Optional<Purchase> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT p FROM Purchase p WHERE p.user.id = :userId AND p.purchaseDate BETWEEN :startDate AND :endDate ORDER BY p.purchaseDate DESC")
    List<Purchase> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.user.id = :userId AND p.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PurchaseStatus status);

    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.items WHERE p.id IN :ids")
    List<Purchase> findAllWithItemsByIds(@Param("ids") List<Long> ids);

    @Query("SELECT p FROM Purchase p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:userId IS NULL OR p.user.id = :userId) AND " +
           "(:from IS NULL OR p.purchaseDate >= :from) AND " +
           "(:to IS NULL OR p.purchaseDate <= :to) " +
           "ORDER BY p.purchaseDate DESC")
    Page<Purchase> findAllWithFilters(@Param("status") PurchaseStatus status,
                                      @Param("userId") Long userId,
                                      @Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to,
                                      Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE p.purchaseDate BETWEEN :from AND :to")
    List<Purchase> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.status = :status AND p.purchaseDate BETWEEN :from AND :to")
    Long countByStatusAndDateRange(@Param("status") PurchaseStatus status, 
                                   @Param("from") LocalDateTime from, 
                                   @Param("to") LocalDateTime to);

    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.status = 'COMPLETED' AND p.purchaseDate BETWEEN :from AND :to")
    BigDecimal sumTotalAmountByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}