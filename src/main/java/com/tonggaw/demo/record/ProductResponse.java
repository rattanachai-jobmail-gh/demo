package com.tonggaw.demo.record;

public record ProductResponse(
    String productSpu,
    String productSku,
    String productName,
    String unitOfMeasure,
    int productAmount,
    double productSellingPricePerUnit,
    double productCostPricePerUnit,
    String productBarCode,
    String username
) {}
