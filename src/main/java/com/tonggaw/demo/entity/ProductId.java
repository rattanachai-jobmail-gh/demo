package com.tonggaw.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class ProductId implements Serializable {
    private String productSku;
    private String productSpu;
    

    public ProductId() {}

    public ProductId(String productSpu, String productSku) {
        this.productSku = productSku;
        this.productSpu = productSpu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId that = (ProductId) o;
        return Objects.equals(productSku, that.productSku) &&
               Objects.equals(productSpu, that.productSpu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productSku, productSpu);
    }
}
