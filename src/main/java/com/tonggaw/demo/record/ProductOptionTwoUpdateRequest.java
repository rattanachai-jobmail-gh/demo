package com.tonggaw.demo.record;

public record ProductOptionTwoUpdateRequest(
    String productSpu,
    String productSku,
    int productAmount
) {
}
