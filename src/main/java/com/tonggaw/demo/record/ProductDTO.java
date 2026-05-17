package com.tonggaw.demo.record;

import java.util.Date;

public record ProductDTO(
    String productSpu,
    String productSku,
    String productName,
    String unitOfMeasure,
    int productAmount,
    double productSellingPricePerUnit,
    Double productCostPricePerUnit,

    boolean receivedDateExisted,
    boolean expiredDateExisted,

    Date receivedDate,
    Date expiredDate,

    String productBarCode
) 
{
    
}