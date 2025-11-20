package com.vetcare_back.controller.purchase;

import com.vetcare_back.dto.purchase.*;
import com.vetcare_back.entity.PurchaseStatus;
import com.vetcare_back.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;



    @GetMapping
    public ResponseEntity<Page<PurchaseResponseDTO>> getUserPurchases(Pageable pageable) {
        Page<PurchaseResponseDTO> response = purchaseService.getCurrentUserPurchases(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/buy-now")
    public ResponseEntity<PurchaseResponseDTO> buyNow(@Valid @RequestBody BuyNowDTO dto) {
        PurchaseResponseDTO response = purchaseService.buyNow(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/from-cart")
    public ResponseEntity<PurchaseResponseDTO> purchaseFromCart() {
        PurchaseResponseDTO response = purchaseService.purchaseFromCart();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{purchaseId}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<PurchaseResponseDTO> completePurchase(@PathVariable Long purchaseId) {
        PurchaseResponseDTO response = purchaseService.completePurchase(purchaseId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{purchaseId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<PurchaseResponseDTO> cancelPurchase(@PathVariable Long purchaseId) {
        PurchaseResponseDTO response = purchaseService.cancelPurchase(purchaseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{purchaseId}")
    public ResponseEntity<PurchaseResponseDTO> getPurchaseById(@PathVariable Long purchaseId) {
        PurchaseResponseDTO response = purchaseService.getPurchaseById(purchaseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Page<PurchaseResponseDTO>> getAllPurchases(
            @RequestParam(required = false) PurchaseStatus status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        Page<PurchaseResponseDTO> response = purchaseService.getAllPurchases(status, userId, from, to, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/manual")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<PurchaseResponseDTO> createManualPurchase(@Valid @RequestBody ManualPurchaseDTO dto) {
        PurchaseResponseDTO response = purchaseService.createManualPurchase(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StatisticsDTO> getStatistics(
            @RequestParam(defaultValue = "LAST_30_DAYS") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        StatisticsDTO response = purchaseService.getStatistics(period, from, to);
        return ResponseEntity.ok(response);
    }
}