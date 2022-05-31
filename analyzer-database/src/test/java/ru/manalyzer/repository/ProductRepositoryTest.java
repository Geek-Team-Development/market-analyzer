package ru.manalyzer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import ru.manalyzer.persist.Product;

import java.math.BigDecimal;

@DataMongoTest
@OverrideAutoConfiguration(enabled = true)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private static Product expectedProduct;

    @BeforeAll
    public static void init() {
        expectedProduct = new Product();
        expectedProduct.setProductShopId("001");
        expectedProduct.setShopName("MVideo");
        expectedProduct.setName("Macbook");
        expectedProduct.setCost(new BigDecimal(200000));
        expectedProduct.setImageLink("");
        expectedProduct.setProductLink("");
    }

    @Test
    public void findProductByShopIdAndShopNameTest() {
        productRepository.save(expectedProduct);

        Product savedProduct = productRepository.findByProductShopIdAndShopName(
                expectedProduct.getProductShopId(),
                expectedProduct.getShopName()
        ).get();

        Assertions.assertEquals(expectedProduct.getProductShopId(), savedProduct.getProductShopId());
        Assertions.assertEquals(expectedProduct.getShopName(), savedProduct.getShopName());
        Assertions.assertEquals(expectedProduct.getName(), savedProduct.getName());
        Assertions.assertEquals(expectedProduct.getCost(), savedProduct.getCost());
        Assertions.assertEquals(expectedProduct.getImageLink(), savedProduct.getImageLink());
        Assertions.assertEquals(expectedProduct.getProductLink(), savedProduct.getProductLink());

        productRepository.delete(expectedProduct);
    }

    @Test
    public void productNotFoundTest() {
        Assertions.assertFalse(
                productRepository.findByProductShopIdAndShopName("unknown", "unknown")
                        .isPresent()
        );
    }

    @Test
    public void failDuplicateProductByShopIdAndShopNameTest() {
        productRepository.save(expectedProduct);

        Product duplicateProduct = new Product();
        duplicateProduct.setProductShopId("001");
        duplicateProduct.setShopName("MVideo");
        duplicateProduct.setName("Macbook Pro");
        duplicateProduct.setCost(new BigDecimal(180000));
        duplicateProduct.setImageLink("");
        duplicateProduct.setProductLink("");

        Assertions.assertThrows(DuplicateKeyException.class, () -> productRepository.save(duplicateProduct));

        productRepository.delete(expectedProduct);
    }

}
