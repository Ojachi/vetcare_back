package com.vetcare_back.controller.purchase;

import com.vetcare_back.dto.purchase.BuyNowDTO;
import com.vetcare_back.dto.purchase.PurchaseResponseDTO;
import com.vetcare_back.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public ResponseEntity<PurchaseResponseDTO> completePurchase(@PathVariable Long purchaseId) {
        PurchaseResponseDTO response = purchaseService.completePurchase(purchaseId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{purchaseId}/cancel")
    public ResponseEntity<PurchaseResponseDTO> cancelPurchase(@PathVariable Long purchaseId) {
        PurchaseResponseDTO response = purchaseService.cancelPurchase(purchaseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{purchaseId}")
    public ResponseEntity<PurchaseResponseDTO> getPurchaseById(@PathVariable Long purchaseId) {
        PurchaseResponseDTO response = purchaseService.getPurchaseById(purchaseId);
        return ResponseEntity.ok(response);
    }


}