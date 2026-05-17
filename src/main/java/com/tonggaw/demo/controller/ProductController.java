package com.tonggaw.demo.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import com.tonggaw.demo.entity.Product;
import com.tonggaw.demo.entity.User;
import com.tonggaw.demo.record.ProductDTO;
import com.tonggaw.demo.record.ProductOptionOneUpdateRequest;
import com.tonggaw.demo.record.ProductOptionThreeUpdateRequest;
import com.tonggaw.demo.record.ProductOptionTwoUpdateRequest;
import com.tonggaw.demo.record.ProductResponse;
import com.tonggaw.demo.record.SearchProductDTO;
import com.tonggaw.demo.security.CustomUserDetails;
import com.tonggaw.demo.service.ProductService;
import com.tonggaw.demo.service.UserService;


@RestController 
@RequestMapping("/productApi")
public class ProductController {

    private final ProductService productService;
    private UserService userService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addNew")
    public ResponseEntity<?> addNewProduct(@RequestBody ProductDTO productDTO, Authentication authentication) {
        Product product = new Product();
        String spuId = productDTO.productSpu();
        String skuId = productDTO.productSku();
        if (productService.existsByProductSpuAndProductSku(spuId, skuId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "SPU + SKU ห้ามซ้ำ"));
        }
        product.setProductSpu(spuId);
        product.setProductSku(skuId);

        product.setProductName(productDTO.productName());
        product.setUnitOfMeasure(productDTO.unitOfMeasure());
        product.setProductAmount(productDTO.productAmount()); 
        product.setProductSellingPricePerUnit(productDTO.productSellingPricePerUnit());
        
        product.setProductCostPricePerUnit(
                java.util.Objects.requireNonNullElse(productDTO.productCostPricePerUnit(), 0.0)
        );
        product.setReceivedDateExisted(productDTO.receivedDateExisted());
        product.setExpiredDateExisted(productDTO.expiredDateExisted());
        product.setReceivedDate(productDTO.receivedDate());
        product.setExpiredDate(productDTO.expiredDate());

        System.out.println("productDTO = " + productDTO);
        System.out.println("barcode = " + productDTO.productBarCode());
        Product existingProductByBarCode = productService.findByBarCode(productDTO.productBarCode());
        if (Objects.nonNull(existingProductByBarCode)) {
            return ResponseEntity.badRequest().body(Map.of("message", "barcode ห้ามซ้ำ"));
        }
        product.setProductBarCode(productDTO.productBarCode());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String byUserName = userDetails.getUsername();
        User user = userService.findByUsername(byUserName);
        product.setByUser(user);
        Product saved = productService.addNewProduct(product);
        URI location = URI.create("/productApi/addNew" + saved.getProductSku());

        return ResponseEntity.created(location)
                              .body(Map.of("message", "Product added successfully",
                                            "productSku", saved.getProductSku()));
    }

    
    @GetMapping("/search")
    public ResponseEntity<Page<SearchProductDTO>> searchProducts(
        @RequestParam(defaultValue = "") String keyword,
        Pageable pageable
    ) {
        Page<SearchProductDTO> products = productService.findProductsByKeyword(keyword, pageable)
                                                        .map(p -> new SearchProductDTO(
                                                                    p.getProductSpu(),
                                                                    p.getProductSku(),
                                                                    p.getProductName(),
                                                                    p.getUnitOfMeasure(),
                                                                    p.getProductAmount(),
                                                                    p.getProductSellingPricePerUnit(),
                                                                    p.getProductCostPricePerUnit(),
                                                                    p.isReceivedDateExisted(),
                                                                    p.isExpiredDateExisted(),
                                                                    p.getReceivedDate(),
                                                                    p.getExpiredDate(),
                                                                    p.getProductBarCode()
                                                                )
                                                            );
                                                       
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/getAll")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProduct().stream()
            .map(product -> new ProductResponse(
                product.getProductSpu(),
                product.getProductSku(),
                product.getProductName(),
                product.getUnitOfMeasure(),
                product.getProductAmount(),
                product.getProductSellingPricePerUnit(),
                product.getProductCostPricePerUnit(),
                product.getProductBarCode(),
                product.getByUser() != null ? product.getByUser().getUsername() : null
            ))
            .toList();

        return ResponseEntity.ok(products);
    }

    @PutMapping("/update/option1")
    public ResponseEntity<?> updateProductOptionOne(@RequestBody ProductOptionOneUpdateRequest request) {
        try {
            Product updatedProduct = productService.updateProductOptionOne(request);
            return ResponseEntity.ok(toSearchProductDTO(updatedProduct));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @PutMapping("/update/option2")
    public ResponseEntity<?> updateProductOptionTwo(@RequestBody ProductOptionTwoUpdateRequest request) {
        try {
            Product updatedProduct = productService.updateProductOptionTwo(request);
            return ResponseEntity.ok(toSearchProductDTO(updatedProduct));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @PutMapping("/update/option3")
    public ResponseEntity<?> updateProductOptionThree(@RequestBody ProductOptionThreeUpdateRequest request) {
        try {
            Product updatedProduct = productService.updateProductOptionThree(request);
            return ResponseEntity.ok(toSearchProductDTO(updatedProduct));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    private SearchProductDTO toSearchProductDTO(Product product) {
        return new SearchProductDTO(
                product.getProductSpu(),
                product.getProductSku(),
                product.getProductName(),
                product.getUnitOfMeasure(),
                product.getProductAmount(),
                product.getProductSellingPricePerUnit(),
                product.getProductCostPricePerUnit(),
                product.isReceivedDateExisted(),
                product.isExpiredDateExisted(),
                product.getReceivedDate(),
                product.getExpiredDate(),
                product.getProductBarCode()
        );
    }

}
   
