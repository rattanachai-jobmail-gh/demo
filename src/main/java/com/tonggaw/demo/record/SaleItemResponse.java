package com.tonggaw.demo.record;

public record SaleItemResponse(
    long saleId,
    String saleItemSpu,
    String saleItemSku,
    String itemName,
    String saleItemBarCode,
    String unitOfMeasure,
    double unitPrice,
    int quantity,
    double lineTotal,
    double discountAmount,
    double discountValue,
    double netUnitPrice,
    String discountType
) {
}
