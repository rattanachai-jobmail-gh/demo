package com.tonggaw.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="sale_items")
@IdClass(SaleItemId.class)
@Data
public class SaleItem {

    @Id
    private long saleId;

    @Id
    private String saleItemSpu;

	@Id
    private String saleItemSku;

    String itemName;
    String saleItemBarCode;
    String unitOfMeasure;
    double unitPrice;
    int quantity;
    double lineTotal;
    double discountAmount;
    double discountValue;
    double netUnitPrice;
    String discountType;

}
