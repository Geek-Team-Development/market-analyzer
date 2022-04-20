package ru.manalyzer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.manalyzer.persist.Product;
import ru.manalyzer.persist.ProductPrice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@DataMongoTest(excludeAutoConfiguration= {EmbeddedMongoAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
public class ProductPricesTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    private static Product testProductMacbook;

    @BeforeAll
    public static void init() {
        testProductMacbook = new Product();
        testProductMacbook.setProductShopId("001");
        testProductMacbook.setShopName("MVideo");
        testProductMacbook.setName("Macbook");
        testProductMacbook.setCost(new BigDecimal(200000));
        testProductMacbook.setImageLink("");
        testProductMacbook.setProductLink("");
    }

    @Test
    public void findByProductIdTest() {
        productRepository.save(testProductMacbook);

        ProductPrice productPrice1 = new ProductPrice();
        productPrice1.setProductId(testProductMacbook.getId());
        productPrice1.setPrice(new BigDecimal(190000));
        productPrice1.setDate(new Date());

        ProductPrice productPrice2 = new ProductPrice();
        productPrice2.setProductId(testProductMacbook.getId());
        productPrice2.setPrice(new BigDecimal(200000));
        productPrice2.setDate(new Date());

        productPriceRepository.save(productPrice1);
        productPriceRepository.save(productPrice2);

        List<ProductPrice> savedProductPrices = productPriceRepository.findByProductId(testProductMacbook.getId());

        Assertions.assertEquals(2, savedProductPrices.size());
        savedProductPrices.forEach(
                price -> Assertions.assertEquals(testProductMacbook.getId(), price.getProductId())
        );

        productPriceRepository.delete(productPrice1);
        productPriceRepository.delete(productPrice2);
        productRepository.delete(testProductMacbook);
    }

    @Test
    public void productPriceNotFoundTest() {
        productRepository.save(testProductMacbook);

        Assertions.assertTrue(productPriceRepository.findByProductId(testProductMacbook.getId()).isEmpty());

        productRepository.delete(testProductMacbook);
    }
}
