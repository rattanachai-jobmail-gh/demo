package com.tonggaw.demo.record;

import java.util.List;

public record SaleCheckoutRequest(
    String paymentMethod,
    double receivedAmount,
    String billDiscountType,
    double billDiscountValue,
    double billDiscountAmount,
    String note,
    List<SaleCheckoutItemRequest> items
) {
}
