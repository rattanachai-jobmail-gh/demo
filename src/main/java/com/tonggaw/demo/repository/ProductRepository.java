package com.tonggaw.demo.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tonggaw.demo.entity.Product;
import com.tonggaw.demo.entity.ProductId;

public interface ProductRepository extends CrudRepository<Product, ProductId> {
    Product findByProductBarCode(String barcode);
    boolean existsByProductSpuAndProductSku(String productSpu, String productSku);
    
    @Query("""
        SELECT p
        FROM Product p
        WHERE (:kw IS NULL OR :kw = '')
        OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :kw, '%'))
        OR LOWER(p.productSpu) LIKE LOWER(CONCAT('%', :kw, '%'))
        OR LOWER(p.productSku) LIKE LOWER(CONCAT('%', :kw, '%'))
        OR LOWER(p.productBarCode) LIKE LOWER(CONCAT('%', :kw, '%'))
        ORDER BY p.productAmount ASC
    """)
    Page<Product> findProductsByKeyword(@Param("kw") String keyword, Pageable pageable);
}