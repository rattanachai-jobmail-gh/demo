package com.tonggaw.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.tonggaw.demo.entity.SaleBill;

public interface SaleRepository extends CrudRepository<SaleBill, Long> {
}
