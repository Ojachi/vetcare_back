package com.vetcare_back.controller.cart;

import com.vetcare_back.dto.cart.AddToCartDTO;
import com.vetcare_back.dto.cart.CartResponseDTO;
import com.vetcare_back.dto.cart.UpdateCartItemDTO;
import com.vetcare_back.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;



    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@Valid @RequestBody AddToCartDTO dto) {
        CartResponseDTO response = cartService.addToCart(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemDTO dto) {
        
        CartResponseDTO response = cartService.updateCartItem(itemId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(@PathVariable Long itemId) {
        CartResponseDTO response = cartService.removeFromCart(itemId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        CartResponseDTO response = cartService.getCurrentUserCart();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCurrentUserCart();
        return ResponseEntity.ok().build();
    }


}