package com.tonggaw.demo.record;

public record SaleCheckoutItemRequest(
    String productSpu,
    String productSku,
    int quantity,
    String discountType,
    double discountValue
) {
}
