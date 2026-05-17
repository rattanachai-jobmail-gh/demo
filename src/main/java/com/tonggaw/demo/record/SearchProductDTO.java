package com.tonggaw.demo.record;

import java.util.Date;

public record SearchProductDTO(
    String searchProductSpu,
    String searchProductSku,
    String searchProductName,
    String searchUnitOfMeasure,
    int searchProductAmount,
    double searchProductSellingPricePerUnit,
    Double searchProductCostPricePerUnit,

    boolean searchReceivedDateExisted,
    boolean searchExpiredDateExisted,

    Date searchReceivedDate,
    Date searchExpiredDate,

    String searchProductBarCode
) 
{

 
    
}
