package com.tonggaw.demo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tonggaw.demo.entity.Product;
import com.tonggaw.demo.entity.SaleBill;
import com.tonggaw.demo.entity.SaleItem;
import com.tonggaw.demo.entity.User;
import com.tonggaw.demo.record.SaleCheckoutItemRequest;
import com.tonggaw.demo.record.SaleCheckoutRequest;
import com.tonggaw.demo.record.SaleCheckoutResponse;
import com.tonggaw.demo.record.SaleItemResponse;
import com.tonggaw.demo.repository.SaleItemRepository;
import com.tonggaw.demo.repository.SaleRepository;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductService productService;
    private final UserService userService;

    public SaleService(
            SaleRepository saleRepository,
            SaleItemRepository saleItemRepository,
            ProductService productService,
            UserService userService) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Transactional
    public SaleCheckoutResponse checkoutSale(SaleCheckoutRequest request, String cashierUsername) {
        validateCheckoutRequest(request);

        User cashier = userService.findByUsername(cashierUsername);
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier not found");
        }

        List<SaleItem> saleItems = new ArrayList<>();
        double subtotal = 0;

        for (SaleCheckoutItemRequest itemRequest : request.items()) {
            Product product = productService.getRequiredProduct(itemRequest.productSpu(), itemRequest.productSku());

            if (itemRequest.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }

            if (product.getProductAmount() < itemRequest.quantity()) {
                throw new IllegalArgumentException("สินค้าในสต็อกไม่เพียงพอสำหรับ " + product.getProductName());
            }

            double unitPrice = product.getProductSellingPricePerUnit();
            String itemDiscountType = normalizeDiscountType(itemRequest.discountType());
            double itemDiscountValue = Math.max(0, itemRequest.discountValue());
            double lineDiscountAmount = calculateItemDiscountAmount(
                    unitPrice,
                    itemRequest.quantity(),
                    itemDiscountType,
                    itemDiscountValue
            );
            double grossLineTotal = unitPrice * itemRequest.quantity();
            double lineTotal = Math.max(0, grossLineTotal - lineDiscountAmount);
            double netUnitPrice = itemRequest.quantity() > 0 ? lineTotal / itemRequest.quantity() : unitPrice;
            subtotal += lineTotal;

            SaleItem saleItem = new SaleItem();
            saleItem.setSaleItemSpu(product.getProductSpu());
            saleItem.setSaleItemSku(product.getProductSku());
            saleItem.setItemName(product.getProductName());
            saleItem.setSaleItemBarCode(product.getProductBarCode());
            saleItem.setUnitOfMeasure(product.getUnitOfMeasure());
            saleItem.setUnitPrice(unitPrice);
            saleItem.setQuantity(itemRequest.quantity());
            saleItem.setLineTotal(lineTotal);
            saleItem.setDiscountAmount(lineDiscountAmount);
            saleItem.setDiscountValue(itemDiscountValue);
            saleItem.setNetUnitPrice(netUnitPrice);
            saleItem.setDiscountType(itemDiscountType);
            saleItems.add(saleItem);

            productService.decreaseProductAmount(product, itemRequest.quantity());
        }

        String billDiscountType = normalizeDiscountType(request.billDiscountType());
        double billDiscountValue = Math.max(0, request.billDiscountValue());
        double billDiscountAmount = calculateBillDiscountAmount(subtotal, billDiscountType, billDiscountValue);
        double grandTotal = Math.max(0, subtotal - billDiscountAmount);
        double receivedAmount = Math.max(0, request.receivedAmount());

        if ("CASH".equalsIgnoreCase(request.paymentMethod()) && receivedAmount < grandTotal) {
            throw new IllegalArgumentException("Received amount is not enough");
        }

        SaleBill saleBill = new SaleBill();
        saleBill.setCashierUsername(cashier.getUsername());
        saleBill.setCashierFirstName(cashier.getUserFirstName());
        saleBill.setCashierLastName(cashier.getUserLastName());
        saleBill.setPaymentMethod(request.paymentMethod().toUpperCase());
        saleBill.setSubtotal(subtotal);
        saleBill.setGrandTotal(grandTotal);
        saleBill.setReceivedAmount(receivedAmount);
        saleBill.setChangeAmount(Math.max(0, receivedAmount - grandTotal));
        saleBill.setBillDiscountType(billDiscountType);
        saleBill.setBillDiscountValue(billDiscountValue);
        saleBill.setBillDiscountAmount(billDiscountAmount);
        saleBill.setNote(request.note());
        saleBill.setSaleDate(new Date());

        SaleBill savedSaleBill = saleRepository.save(saleBill);

        for (SaleItem saleItem : saleItems) {
            saleItem.setSaleId(savedSaleBill.getSaleId());
        }
        saleItemRepository.saveAll(saleItems);

        return toCheckoutResponse(savedSaleBill, saleItems);
    }

    public List<SaleCheckoutResponse> getAllSales() {
        List<SaleCheckoutResponse> responses = new ArrayList<>();
        for (SaleBill saleBill : saleRepository.findAll()) {
            responses.add(toCheckoutResponse(saleBill, saleItemRepository.findBySaleId(saleBill.getSaleId())));
        }
        return responses;
    }

    public SaleCheckoutResponse getSaleById(long saleId) {
        SaleBill saleBill = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale not found"));
        return toCheckoutResponse(saleBill, saleItemRepository.findBySaleId(saleId));
    }

    private void validateCheckoutRequest(SaleCheckoutRequest request) {
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Sale items are required");
        }

        if (request.paymentMethod() == null || request.paymentMethod().isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }
    }

    private String normalizeDiscountType(String discountType) {
        return discountType == null || discountType.isBlank() ? "NONE" : discountType.toUpperCase();
    }

    private double calculateBillDiscountAmount(double subtotal, String discountType, double discountValue) {
        if (subtotal <= 0) {
            return 0;
        }

        return switch (discountType) {
            case "PERCENT" -> {
                double normalizedPercent = Math.min(100, Math.max(0, discountValue));
                yield Math.min(subtotal, subtotal * (normalizedPercent / 100));
            }
            case "AMOUNT" -> Math.min(subtotal, Math.max(0, discountValue));
            default -> 0;
        };
    }

    private double calculateItemDiscountAmount(double unitPrice, int quantity, String discountType, double discountValue) {
        double grossLineTotal = unitPrice * quantity;
        if (grossLineTotal <= 0) {
            return 0;
        }

        return switch (discountType) {
            case "PERCENT" -> {
                double normalizedPercent = Math.min(100, Math.max(0, discountValue));
                yield Math.min(grossLineTotal, grossLineTotal * (normalizedPercent / 100));
            }
            case "AMOUNT" -> Math.min(grossLineTotal, Math.max(0, discountValue));
            default -> 0;
        };
    }

    private SaleCheckoutResponse toCheckoutResponse(SaleBill saleBill, List<SaleItem> saleItems) {
        return new SaleCheckoutResponse(
                saleBill.getSaleId(),
                saleBill.getCashierUsername(),
                saleBill.getCashierFirstName(),
                saleBill.getCashierLastName(),
                saleBill.getPaymentMethod(),
                saleBill.getSubtotal(),
                saleBill.getGrandTotal(),
                saleBill.getReceivedAmount(),
                saleBill.getChangeAmount(),
                saleBill.getBillDiscountType(),
                saleBill.getBillDiscountValue(),
                saleBill.getBillDiscountAmount(),
                saleBill.getNote(),
                saleBill.getSaleDate(),
                saleItems.stream().map(this::toSaleItemResponse).toList()
        );
    }

    private SaleItemResponse toSaleItemResponse(SaleItem saleItem) {
        return new SaleItemResponse(
                saleItem.getSaleId(),
                saleItem.getSaleItemSpu(),
                saleItem.getSaleItemSku(),
                saleItem.getItemName(),
                saleItem.getSaleItemBarCode(),
                saleItem.getUnitOfMeasure(),
                saleItem.getUnitPrice(),
                saleItem.getQuantity(),
                saleItem.getLineTotal(),
                saleItem.getDiscountAmount(),
                saleItem.getDiscountValue(),
                saleItem.getNetUnitPrice(),
                saleItem.getDiscountType()
        );
    }
}
