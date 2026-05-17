package com.tonggaw.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tonggaw.demo.entity.SaleItem;
import com.tonggaw.demo.entity.SaleItemId;

public interface SaleItemRepository extends CrudRepository<SaleItem, SaleItemId> {
    List<SaleItem> findBySaleId(long saleId);
}
