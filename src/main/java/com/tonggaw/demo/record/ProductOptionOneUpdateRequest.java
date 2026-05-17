package com.tonggaw.demo.record;

import java.util.Date;

public record ProductOptionOneUpdateRequest(
    String originalProductSpu,
    String originalProductSku,
    String productSpu,
    String productSku,
    String productName,
    String unitOfMeasure,
    double productSellingPricePerUnit,
    boolean receivedDateExisted,
    boolean expiredDateExisted,
    Date receivedDate,
    Date expiredDate,
    String productBarCode
) {
}
