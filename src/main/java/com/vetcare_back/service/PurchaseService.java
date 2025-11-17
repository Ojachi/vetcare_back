package com.vetcare_back.service;

import com.vetcare_back.dto.purchase.BuyNowDTO;
import com.vetcare_back.dto.purchase.PurchaseResponseDTO;
import com.vetcare_back.entity.*;
import com.vetcare_back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseItemRepository purchaseItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public PurchaseResponseDTO buyNow(BuyNowDTO dto) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Stock insuficiente");
        }

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setStatus(PurchaseStatus.PENDING);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        purchase = purchaseRepository.save(purchase);

        PurchaseItem item = new PurchaseItem();
        item.setPurchase(purchase);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setQuantity(dto.getQuantity());
        item.setPrice(product.getPrice());
        item.calculateSubtotal();
        purchaseItemRepository.save(item);
        
        purchase.addItem(item);

        // Reducir stock
        product.setStock(product.getStock() - dto.getQuantity());
        productRepository.save(product);

        // Recargar con items
        purchase = purchaseRepository.findByIdWithItems(purchase.getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        return PurchaseResponseDTO.fromEntity(purchase);
    }

    public PurchaseResponseDTO purchaseFromCart() {
        Long userId = getCurrentUserId();
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar stock
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }
        }

        // Calcular total
        BigDecimal total = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setStatus(PurchaseStatus.PENDING);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setTotalAmount(total);
        purchase = purchaseRepository.save(purchase);

        // Crear items y reducir stock
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            PurchaseItem item = new PurchaseItem();
            item.setPurchase(purchase);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getPrice());
            item.calculateSubtotal();
            purchaseItemRepository.save(item);
            
            purchase.addItem(item);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Limpiar carrito
        cartItemRepository.deleteByCartId(cart.getId());

        // Recargar con items
        purchase = purchaseRepository.findByIdWithItems(purchase.getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        return PurchaseResponseDTO.fromEntity(purchase);
    }

    public PurchaseResponseDTO completePurchase(Long purchaseId) {
        Long userId = getCurrentUserId();
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (!purchase.getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado");
        }

        purchase.complete();
        purchase = purchaseRepository.save(purchase);

        return PurchaseResponseDTO.fromEntity(purchase);
    }

    public PurchaseResponseDTO cancelPurchase(Long purchaseId) {
        Long userId = getCurrentUserId();
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (!purchase.getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado");
        }

        if (purchase.getStatus() == PurchaseStatus.COMPLETED) {
            throw new RuntimeException("No se puede cancelar una compra completada");
        }

        // Restaurar stock
        List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(purchaseId);
        for (PurchaseItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        purchase.cancel();
        purchase = purchaseRepository.save(purchase);

        return PurchaseResponseDTO.fromEntity(purchase);
    }

    public Page<PurchaseResponseDTO> getCurrentUserPurchases(Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<Purchase> purchases = purchaseRepository.findByUserIdOrderByPurchaseDateDesc(userId, pageable);
        return purchases.map(PurchaseResponseDTO::fromEntity);
    }

    public PurchaseResponseDTO getPurchaseById(Long purchaseId) {
        Long userId = getCurrentUserId();
        Purchase purchase = purchaseRepository.findByIdWithItems(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (!purchase.getUser().getId().equals(userId)) {
            throw new RuntimeException("No autorizado");
        }

        return PurchaseResponseDTO.fromEntity(purchase);
    }

    private Long getCurrentUserId() {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
}