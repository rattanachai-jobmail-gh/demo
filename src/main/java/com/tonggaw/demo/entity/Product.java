package com.tonggaw.demo.entity;

import java.util.Date;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="products")
@IdClass(ProductId.class)
public class Product {
	@Id
    private String productSpu;

	@Id
    private String productSku;

    
	
	private String productName;
	
	
	private String unitOfMeasure;
	
	
	private int productAmount;
	
	
	private double productSellingPricePerUnit;

	private double productCostPricePerUnit;
	
	
	private boolean receivedDateExisted;
	
	
	private boolean expiredDateExisted; 
	
	@Nullable
	private Date receivedDate;
	
	@Nullable
	private Date expiredDate;

	@Getter
	@Setter
	private String productBarCode;
	
	@ManyToOne
	@JoinColumn(name = "username", nullable = true)
	private User byUser;


	public String getProductSku() {
		return productSku;
	}


	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}


	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public boolean isReceivedDateExisted() {
		return receivedDateExisted;
	}


	public void setReceivedDateExisted(boolean receivedDateExisted) {
		this.receivedDateExisted = receivedDateExisted;
	}


	public boolean isExpiredDateExisted() {
		return expiredDateExisted;
	}


	public void setExpiredDateExisted(boolean expiredDateExisted) {
		this.expiredDateExisted = expiredDateExisted;
	}


	public Date getReceivedDate() {
		return receivedDate;
	}


	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}


	public Date getExpiredDate() {
		return expiredDate;
	}


	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}


	public User getByUser() {
		return byUser;
	}


	public void setByUser(User byUser) {
		this.byUser = byUser;
	}

    public String getProductSpu() {
        return productSpu;
    }

    public void setProductSpu(String productSpu) {
        this.productSpu = productSpu;
    }

    public int getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }

    public double getProductSellingPricePerUnit() {
        return productSellingPricePerUnit;
    }

    public void setProductSellingPricePerUnit(double productSellingPricePerUnit) {
        this.productSellingPricePerUnit = productSellingPricePerUnit;
    }

    public double getProductCostPricePerUnit() {
        return productCostPricePerUnit;
    }

    public void setProductCostPricePerUnit(double productCostPricePerUnit) {
        this.productCostPricePerUnit = productCostPricePerUnit;
    }
	
	
	
}
