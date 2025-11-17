package com.vetcare_back.repository;

import com.vetcare_back.entity.Cart;
import com.vetcare_back.entity.CartItem;
import com.vetcare_back.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    List<CartItem> findByCartId(Long cartId);

    List<CartItem> findByCartIdOrderByCreatedAtDesc(Long cartId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartIdWithProduct(@Param("cartId") Long cartId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.user.id = :userId")
    Integer countItemsByUserId(@Param("userId") Long userId);
}