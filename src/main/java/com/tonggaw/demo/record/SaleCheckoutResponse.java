package com.tonggaw.demo.record;

import java.util.Date;
import java.util.List;

public record SaleCheckoutResponse(
    long saleId,
    String cashierUsername,
    String cashierFirstName,
    String cashierLastName,
    String paymentMethod,
    double subtotal,
    double grandTotal,
    double receivedAmount,
    double changeAmount,
    String billDiscountType,
    double billDiscountValue,
    double billDiscountAmount,
    String note,
    Date saleDate,
    List<SaleItemResponse> items
) {
}
