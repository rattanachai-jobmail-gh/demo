package com.tonggaw.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tonggaw.demo.record.SaleCheckoutRequest;
import com.tonggaw.demo.record.SaleCheckoutResponse;
import com.tonggaw.demo.service.SaleService;

@RestController
@RequestMapping("/saleApi")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody SaleCheckoutRequest request, Authentication authentication) {
        try {
            SaleCheckoutResponse response = saleService.checkoutSale(request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<SaleCheckoutResponse>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<?> getSaleById(@PathVariable long saleId) {
        try {
            return ResponseEntity.ok(saleService.getSaleById(saleId));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }
}
