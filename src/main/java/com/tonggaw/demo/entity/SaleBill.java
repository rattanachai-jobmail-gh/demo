package com.tonggaw.demo.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="sale_bills")
@Data
public class SaleBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long saleId;

    String cashierUsername;
    String cashierFirstName;
    String cashierLastName;
    String paymentMethod;
    double subtotal;
    double grandTotal;
    double receivedAmount;
    double changeAmount;
    String billDiscountType;
    double billDiscountValue;
    double billDiscountAmount;
    String note;
    Date saleDate;
    

}
