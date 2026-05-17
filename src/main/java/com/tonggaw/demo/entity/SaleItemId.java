package com.tonggaw.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class SaleItemId implements Serializable {
    private long saleId;
    private String saleItemSku;
    private String saleItemSpu;
    

    public SaleItemId() {}

    public SaleItemId(long saleId, String saleItemSpu, String saleItemSku) {
        this.saleId = saleId;
        this.saleItemSku = saleItemSku;
        this.saleItemSpu = saleItemSpu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleItemId that = (SaleItemId) o;
        return saleId == that.saleId &&
               Objects.equals(saleItemSku, that.saleItemSku) &&
               Objects.equals(saleItemSpu, that.saleItemSpu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId, saleItemSku, saleItemSpu);
    }
}
