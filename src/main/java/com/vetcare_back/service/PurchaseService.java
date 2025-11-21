package com.vetcare_back.service;

import com.vetcare_back.dto.purchase.*;
import com.vetcare_back.entity.*;
import com.vetcare_back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @CacheEvict(value = "statistics", allEntries = true)
    public PurchaseResponseDTO buyNow(BuyNowDTO dto) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!product.getActive()) {
            throw new RuntimeException("El producto ya no está disponible");
        }

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

    @CacheEvict(value = "statistics", allEntries = true)
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

        // Verificar productos activos y stock
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (!product.getActive()) {
                throw new RuntimeException("El producto '" + product.getName() + "' ya no está disponible");
            }
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

    @CacheEvict(value = "statistics", allEntries = true)
    public PurchaseResponseDTO completePurchase(Long purchaseId) {
        User currentUser = getCurrentUser();
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.EMPLOYEE) {
            throw new RuntimeException("Solo ADMIN o EMPLOYEE pueden completar compras");
        }

        purchase.complete();
        purchase = purchaseRepository.save(purchase);

        return PurchaseResponseDTO.fromEntity(purchase);
    }

    @CacheEvict(value = "statistics", allEntries = true)
    public PurchaseResponseDTO cancelPurchase(Long purchaseId) {
        User currentUser = getCurrentUser();
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.EMPLOYEE) {
            throw new RuntimeException("Solo ADMIN o EMPLOYEE pueden cancelar compras");
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

    public Page<PurchaseResponseDTO> getAllPurchases(PurchaseStatus status, Long userId, 
                                                     LocalDateTime from, LocalDateTime to, 
                                                     Pageable pageable) {
        Page<Purchase> purchases = purchaseRepository.findAllWithFilters(status, userId, from, to, pageable);
        
        List<Long> purchaseIds = purchases.getContent().stream()
                .map(Purchase::getId)
                .toList();
        
        if (purchaseIds.isEmpty()) {
            return purchases.map(PurchaseResponseDTO::fromEntity);
        }
        
        List<Purchase> purchasesWithItems = purchaseRepository.findAllWithItemsByIds(purchaseIds);
        
        return purchases.map(p -> {
            Purchase withItems = purchasesWithItems.stream()
                    .filter(pi -> pi.getId().equals(p.getId()))
                    .findFirst()
                    .orElse(p);
            return PurchaseResponseDTO.fromEntity(withItems);
        });
    }

    @CacheEvict(value = "statistics", allEntries = true)
    public PurchaseResponseDTO createManualPurchase(ManualPurchaseDTO dto) {
        User targetUser = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!targetUser.getRole().equals(Role.OWNER)) {
            throw new RuntimeException("Solo se pueden registrar ventas para clientes (OWNER)");
        }

        for (ManualPurchaseItemDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemDto.getProductId()));
            
            if (!product.getActive()) {
                throw new RuntimeException("Producto inactivo: " + product.getName());
            }
            
            if (product.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }
        }

        BigDecimal total = BigDecimal.ZERO;
        
        Purchase purchase = new Purchase();
        purchase.setUser(targetUser);
        purchase.setStatus(PurchaseStatus.COMPLETED);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setNotes(dto.getNotes());
        purchase.setTotalAmount(BigDecimal.ZERO);
        purchase = purchaseRepository.save(purchase);
        
        for (ManualPurchaseItemDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).get();
            
            PurchaseItem item = new PurchaseItem();
            item.setPurchase(purchase);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(product.getPrice());
            item.calculateSubtotal();
            purchaseItemRepository.save(item);
            
            purchase.addItem(item);
            total = total.add(item.getSubtotal());
            
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);
        }
        
        purchase.setTotalAmount(total);
        purchase = purchaseRepository.save(purchase);
        
        purchase = purchaseRepository.findByIdWithItems(purchase.getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
        
        return PurchaseResponseDTO.fromEntity(purchase);
    }

    @Cacheable(value = "statistics", key = "#period + '-' + #customFrom + '-' + #customTo")
    public StatisticsDTO getStatistics(String period, LocalDateTime customFrom, LocalDateTime customTo) {
        LocalDateTime from;
        LocalDateTime to = LocalDateTime.now();
        
        if ("CUSTOM".equals(period) && customFrom != null && customTo != null) {
            from = customFrom;
            to = customTo;
        } else {
            from = switch (period != null ? period : "LAST_30_DAYS") {
                case "LAST_7_DAYS" -> to.minusDays(7);
                case "LAST_90_DAYS" -> to.minusDays(90);
                case "CURRENT_MONTH" -> to.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                case "CURRENT_YEAR" -> to.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
                default -> to.minusDays(30);
            };
        }
        
        BigDecimal totalAmount = purchaseRepository.sumTotalAmountByDateRange(from, to);
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
        
        Long completedOrders = purchaseRepository.countByStatusAndDateRange(PurchaseStatus.COMPLETED, from, to);
        Long pendingOrders = purchaseRepository.countByStatusAndDateRange(PurchaseStatus.PENDING, from, to);
        Long cancelledOrders = purchaseRepository.countByStatusAndDateRange(PurchaseStatus.CANCELLED, from, to);
        Long totalOrders = completedOrders + pendingOrders + cancelledOrders;
        
        BigDecimal averageOrderValue = completedOrders > 0 ? 
                totalAmount.divide(BigDecimal.valueOf(completedOrders), 2, java.math.RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        
        java.util.Map<String, Long> salesByStatus = new java.util.HashMap<>();
        salesByStatus.put("COMPLETED", completedOrders);
        salesByStatus.put("PENDING", pendingOrders);
        salesByStatus.put("CANCELLED", cancelledOrders);
        
        List<PurchaseItem> items = purchaseItemRepository.findCompletedItemsByDateRange(from, to);
        
        java.util.Map<Long, TopSellingProductDTO> productStats = new java.util.HashMap<>();
        for (PurchaseItem item : items) {
            Long productId = item.getProduct().getId();
            productStats.compute(productId, (k, v) -> {
                if (v == null) {
                    return TopSellingProductDTO.builder()
                            .productId(productId)
                            .productName(item.getProductName())
                            .quantitySold((long) item.getQuantity())
                            .revenue(item.getSubtotal())
                            .build();
                } else {
                    v.setQuantitySold(v.getQuantitySold() + item.getQuantity());
                    v.setRevenue(v.getRevenue().add(item.getSubtotal()));
                    return v;
                }
            });
        }
        
        List<TopSellingProductDTO> topProducts = productStats.values().stream()
                .sorted((a, b) -> b.getQuantitySold().compareTo(a.getQuantitySold()))
                .limit(5)
                .toList();
        
        java.util.Map<String, SalesByCategoryDTO> categoryStats = new java.util.HashMap<>();
        for (PurchaseItem item : items) {
            if (item.getProduct().getCategory() != null) {
                String categoryName = item.getProduct().getCategory().getName();
                Long categoryId = item.getProduct().getCategory().getId();
                categoryStats.compute(categoryName, (k, v) -> {
                    if (v == null) {
                        return SalesByCategoryDTO.builder()
                                .categoryId(categoryId)
                                .categoryName(categoryName)
                                .revenue(item.getSubtotal())
                                .orderCount(1L)
                                .build();
                    } else {
                        v.setRevenue(v.getRevenue().add(item.getSubtotal()));
                        v.setOrderCount(v.getOrderCount() + 1);
                        return v;
                    }
                });
            }
        }
        
        List<SalesByCategoryDTO> salesByCategory = categoryStats.values().stream()
                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue()))
                .toList();
        
        return StatisticsDTO.builder()
                .period(period != null ? period : "LAST_30_DAYS")
                .dateFrom(from)
                .dateTo(to)
                .totalAmount(totalAmount)
                .totalOrders(totalOrders)
                .averageOrderValue(averageOrderValue)
                .salesByStatus(salesByStatus)
                .topSellingProducts(topProducts)
                .salesByCategory(salesByCategory)
                .build();
    }

    private Long getCurrentUserId() {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }

    private User getCurrentUser() {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}