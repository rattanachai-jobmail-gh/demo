package com.tonggaw.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tonggaw.demo.entity.Product;
import com.tonggaw.demo.entity.ProductId;
import com.tonggaw.demo.repository.ProductRepository;
import com.tonggaw.demo.record.ProductOptionOneUpdateRequest;
import com.tonggaw.demo.record.ProductOptionThreeUpdateRequest;
import com.tonggaw.demo.record.ProductOptionTwoUpdateRequest;

@Service
public class ProductService {

    private final ProductRepository productRepository;



    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Product addNewProduct(Product product) {
    
        return productRepository.save(product);
    }


    public Product findByBarCode(String barcode) {
    
        return productRepository.findByProductBarCode(barcode);

    }

    public boolean existsByProductSpuAndProductSku(String productSpu, String productSku) {
        return productRepository.existsByProductSpuAndProductSku(productSpu, productSku);
    }

    public Page<Product> findProductsByKeyword(String keyword, Pageable pageable) {
        return productRepository.findProductsByKeyword(keyword, pageable);
    }

    public List<Product> getAllProduct() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .toList();
    }

    public Product getRequiredProduct(String productSpu, String productSku) {
        return productRepository.findById(new ProductId(productSpu, productSku))
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @Transactional
    public Product updateProductOptionOne(ProductOptionOneUpdateRequest request) {
        Product existingProduct = getRequiredProduct(request.originalProductSpu(), request.originalProductSku());
        validateDuplicateBarcode(existingProduct, request.productBarCode());

        boolean changingIdentity =
                !Objects.equals(existingProduct.getProductSpu(), request.productSpu())
                || !Objects.equals(existingProduct.getProductSku(), request.productSku());

        if (changingIdentity && existsByProductSpuAndProductSku(request.productSpu(), request.productSku())) {
            throw new IllegalArgumentException("SPU + SKU ซ้ำ");
        }

        if (!changingIdentity) {
            applyOptionOneFields(existingProduct, request);
            return productRepository.save(existingProduct);
        }

        Product replacementProduct = new Product();
        replacementProduct.setByUser(existingProduct.getByUser());
        replacementProduct.setProductAmount(existingProduct.getProductAmount());
        replacementProduct.setProductCostPricePerUnit(existingProduct.getProductCostPricePerUnit());
        applyOptionOneFields(replacementProduct, request);

        Product savedProduct = productRepository.save(replacementProduct);
        productRepository.deleteById(new ProductId(existingProduct.getProductSpu(), existingProduct.getProductSku()));
        return savedProduct;
    }

    @Transactional
    public Product updateProductOptionTwo(ProductOptionTwoUpdateRequest request) {
        Product existingProduct = getRequiredProduct(request.productSpu(), request.productSku());
        existingProduct.setProductAmount(Math.max(0, request.productAmount()));
        return productRepository.save(existingProduct);
    }

    @Transactional
    public Product updateProductOptionThree(ProductOptionThreeUpdateRequest request) {
        Product existingProduct = getRequiredProduct(request.productSpu(), request.productSku());
        existingProduct.setProductCostPricePerUnit(Math.max(0, request.productCostPricePerUnit()));
        return productRepository.save(existingProduct);
    }

    @Transactional
    public Product decreaseProductAmount(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (product.getProductAmount() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        product.setProductAmount(product.getProductAmount() - quantity);
        return productRepository.save(product);
    }

    private void applyOptionOneFields(Product product, ProductOptionOneUpdateRequest request) {
        product.setProductSpu(request.productSpu());
        product.setProductSku(request.productSku());
        product.setProductName(request.productName());
        product.setUnitOfMeasure(request.unitOfMeasure());
        product.setProductSellingPricePerUnit(request.productSellingPricePerUnit());
        product.setReceivedDateExisted(request.receivedDateExisted());
        product.setExpiredDateExisted(request.expiredDateExisted());
        product.setReceivedDate(request.receivedDateExisted() ? request.receivedDate() : null);
        product.setExpiredDate(request.expiredDateExisted() ? request.expiredDate() : null);
        product.setProductBarCode(request.productBarCode());
    }

    private void validateDuplicateBarcode(Product existingProduct, String candidateBarcode) {
        Product productWithSameBarcode = productRepository.findByProductBarCode(candidateBarcode);

        if (productWithSameBarcode == null) {
            return;
        }

        boolean isSameProduct =
                Objects.equals(productWithSameBarcode.getProductSpu(), existingProduct.getProductSpu())
                && Objects.equals(productWithSameBarcode.getProductSku(), existingProduct.getProductSku());

        if (!isSameProduct) {
            throw new IllegalArgumentException("barcode ห้ามซ้ำ");
        }
    }

}
