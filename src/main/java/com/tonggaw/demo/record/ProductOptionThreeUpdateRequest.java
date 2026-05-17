package com.tonggaw.demo.record;

public record ProductOptionThreeUpdateRequest(
    String productSpu,
    String productSku,
    double productCostPricePerUnit
) {
}
