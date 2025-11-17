package com.vetcare_back.service;

import com.vetcare_back.dto.cart.AddToCartDTO;
import com.vetcare_back.dto.cart.CartResponseDTO;
import com.vetcare_back.dto.cart.UpdateCartItemDTO;
import com.vetcare_back.entity.Cart;
import com.vetcare_back.entity.CartItem;
import com.vetcare_back.entity.Product;
import com.vetcare_back.entity.User;
import com.vetcare_back.repository.CartItemRepository;
import com.vetcare_back.repository.CartRepository;
import com.vetcare_back.repository.ProductRepository;
import com.vetcare_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public CartResponseDTO addToCart(AddToCartDTO dto) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Stock insuficiente");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + dto.getQuantity();
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Stock insuficiente para la cantidad total");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(dto.getQuantity());
            newItem.setPrice(product.getPrice());
            cartItemRepository.save(newItem);
        }

        // Recargar carrito con items
        cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        return CartResponseDTO.fromEntity(cart);
    }

    public CartResponseDTO updateCartItem(Long itemId, UpdateCartItemDTO dto) {
        Long userId = getCurrentUserId();
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado");
        }

        if (dto.getQuantity() <= 0) {
            cartItemRepository.delete(item);
        } else {
            if (item.getProduct().getStock() < dto.getQuantity()) {
                throw new RuntimeException("Stock insuficiente");
            }
            item.setQuantity(dto.getQuantity());
            cartItemRepository.save(item);
        }

        // Recargar carrito con items
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElse(null);
        
        return CartResponseDTO.fromEntity(cart);
    }

    public CartResponseDTO removeFromCart(Long itemId) {
        Long userId = getCurrentUserId();
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado");
        }

        cartItemRepository.delete(item);
        
        // Recargar carrito con items
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElse(null);
        
        return CartResponseDTO.fromEntity(cart);
    }

    public CartResponseDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElse(null);

        if (cart == null) {
            return new CartResponseDTO();
        }

        return CartResponseDTO.fromEntity(cart);
    }

    public CartResponseDTO getCurrentUserCart() {
        Long userId = getCurrentUserId();
        return getCartByUserId(userId);
    }

    public void clearCurrentUserCart() {
        Long userId = getCurrentUserId();
        clearCart(userId);
    }

    private void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cartItemRepository.deleteByCartId(cart.getId());
        }
    }

    private Long getCurrentUserId() {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
}