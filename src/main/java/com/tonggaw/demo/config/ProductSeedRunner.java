package com.tonggaw.demo.config;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tonggaw.demo.entity.Product;
import com.tonggaw.demo.service.ProductService;

@Component
public class ProductSeedRunner implements CommandLineRunner {

    private final ProductService productService;

    public ProductSeedRunner(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) {
        List<SeedProduct> seedProducts = List.of(
            new SeedProduct("SEED-SPU-001", "SEED-SKU-001", "ข้าวหอมมะลิ 5 กก.", "ถุง", 18, 245, 210, "8857000000011"),
            new SeedProduct("SEED-SPU-002", "SEED-SKU-002", "น้ำปลาแท้ 700 มล.", "ขวด", 30, 42, 31, "8857000000012"),
            new SeedProduct("SEED-SPU-003", "SEED-SKU-003", "น้ำตาลทรายขาว 1 กก.", "ถุง", 24, 31, 24, "8857000000013"),
            new SeedProduct("SEED-SPU-004", "SEED-SKU-004", "บะหมี่กึ่งสำเร็จรูปรสหมูสับ", "ซอง", 96, 8, 6, "8857000000014"),
            new SeedProduct("SEED-SPU-005", "SEED-SKU-005", "นมยูเอชที รสจืด 180 มล.", "กล่อง", 72, 14, 10, "8857000000015"),
            new SeedProduct("SEED-SPU-006", "SEED-SKU-006", "ปลากระป๋องซอสมะเขือเทศ", "กระป๋อง", 48, 24, 18, "8857000000016"),
            new SeedProduct("SEED-SPU-007", "SEED-SKU-007", "น้ำดื่ม 600 มล.", "ขวด", 120, 7, 4, "8857000000017"),
            new SeedProduct("SEED-SPU-008", "SEED-SKU-008", "กาแฟกระป๋องลาเต้", "กระป๋อง", 36, 18, 13, "8857000000018"),
            new SeedProduct("SEED-SPU-009", "SEED-SKU-009", "ผงซักฟอกสูตรเข้มข้น 800 กรัม", "ถุง", 20, 59, 45, "8857000000019"),
            new SeedProduct("SEED-SPU-010", "SEED-SKU-010", "น้ำยาล้างจาน 500 มล.", "ขวด", 22, 35, 26, "8857000000020"),
            new SeedProduct("SEED-SPU-011", "SEED-SKU-011", "สบู่ก้อนสมุนไพร", "ก้อน", 60, 19, 12, "8857000000021"),
            new SeedProduct("SEED-SPU-012", "SEED-SKU-012", "ยาสีฟัน 150 กรัม", "หลอด", 28, 52, 39, "8857000000022"),
            new SeedProduct("SEED-SPU-013", "SEED-SKU-013", "แชมพู 450 มล.", "ขวด", 26, 109, 84, "8857000000023"),
            new SeedProduct("SEED-SPU-014", "SEED-SKU-014", "ครีมอาบน้ำ 450 มล.", "ขวด", 24, 115, 88, "8857000000024"),
            new SeedProduct("SEED-SPU-015", "SEED-SKU-015", "ขนมปังแซนด์วิช", "แพ็ค", 15, 32, 24, "8857000000025"),
            new SeedProduct("SEED-SPU-016", "SEED-SKU-016", "ไข่ไก่เบอร์ 2", "แผง", 18, 129, 112, "8857000000026"),
            new SeedProduct("SEED-SPU-017", "SEED-SKU-017", "ซอสหอยนางรม 600 มล.", "ขวด", 16, 58, 43, "8857000000027"),
            new SeedProduct("SEED-SPU-018", "SEED-SKU-018", "น้ำส้มสายชู 700 มล.", "ขวด", 18, 27, 19, "8857000000028"),
            new SeedProduct("SEED-SPU-019", "SEED-SKU-019", "ปลาหมึกอบปรุงรส", "ซอง", 25, 39, 28, "8857000000029"),
            new SeedProduct("SEED-SPU-020", "SEED-SKU-020", "ถ่านอัลคาไลน์ AA 4 ก้อน", "แพ็ค", 14, 89, 67, "8857000000030")
        );

        LocalDate receivedBase = LocalDate.of(2026, 5, 1);
        LocalDate expiredBase = LocalDate.of(2026, 10, 1);

        for (int index = 0; index < seedProducts.size(); index++) {
            SeedProduct seed = seedProducts.get(index);
            if (productService.existsByProductSpuAndProductSku(seed.productSpu(), seed.productSku())
                    || productService.findByBarCode(seed.productBarCode()) != null) {
                continue;
            }

            Product product = new Product();
            product.setProductSpu(seed.productSpu());
            product.setProductSku(seed.productSku());
            product.setProductName(seed.productName());
            product.setUnitOfMeasure(seed.unitOfMeasure());
            product.setProductAmount(seed.productAmount());
            product.setProductSellingPricePerUnit(seed.productSellingPricePerUnit());
            product.setProductCostPricePerUnit(seed.productCostPricePerUnit());
            product.setReceivedDateExisted(true);
            product.setExpiredDateExisted(true);
            product.setReceivedDate(Date.valueOf(receivedBase.plusDays(index)));
            product.setExpiredDate(Date.valueOf(expiredBase.plusDays(index * 3L)));
            product.setProductBarCode(seed.productBarCode());

            productService.addNewProduct(product);
        }
    }

    private record SeedProduct(
        String productSpu,
        String productSku,
        String productName,
        String unitOfMeasure,
        int productAmount,
        double productSellingPricePerUnit,
        double productCostPricePerUnit,
        String productBarCode
    ) {
    }
}
